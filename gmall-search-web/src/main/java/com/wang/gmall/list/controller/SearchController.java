package com.wang.gmall.list.controller;

import com.wang.gmall.annotations.LoginRequire;
import com.wang.gmall.bean.*;
import com.wang.gmall.list.util.JedisClientUtil;
import com.wang.gmall.service.AttrService;
import com.wang.gmall.service.SearchService;
import com.wang.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class SearchController {

  @Reference
  SkuService skuService;

  @Reference
  SearchService searchService;

  @Reference
  AttrService attrService;

  @RequestMapping("index.html")
  @LoginRequire(mustLoginSuccess = false)
  public String index(){
    return "index";
  }

  @RequestMapping("list.html")
  public String list(PmsSearchSkuParam searchSkuParam, ModelMap modelMap) throws Exception {

    //搜索
    List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(searchSkuParam);
    modelMap.put("skuLsInfoList",pmsSearchSkuInfos);

    Set<Integer> valueIdSet = new HashSet<>();
    for(PmsSearchSkuInfo pmsSearchSkuInfo:pmsSearchSkuInfos){
      List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSearchSkuInfo.getSkuAttrValueList();
      for(PmsSkuAttrValue pmsSkuAttrValue:pmsSkuAttrValues){
        Integer valueId = pmsSkuAttrValue.getValueId();
        valueIdSet.add(valueId);
      }
    }
    //根据valueId查属性列表
    List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttrValueListByValueIds(valueIdSet);

    //去除当前条件valueId的属性组
    Integer[] valueIds = searchSkuParam.getValueId();
    if(valueIds!=null) {
      List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
      Map<Integer,String> map = new HashMap<>();
      for (Integer valueId : valueIds) {
        Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
        PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
        pmsSearchCrumb.setValueId(valueId);
        pmsSearchCrumb.setUrlParam(getUrlParam(searchSkuParam, valueId));
        while (iterator.hasNext()) {
          PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
          List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrInfo.getAttrValueList();
          for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrValues) {
            Integer baseValueId = pmsBaseAttrValue.getId();
            String valueName = pmsBaseAttrValue.getValueName();
            map.put(baseValueId,valueName);
            if(baseValueId.equals(valueId)){
              pmsSearchCrumb.setValueName(map.get(valueId));
              iterator.remove();
            }
          }
        }
        pmsSearchCrumbs.add(pmsSearchCrumb);
      }
      modelMap.put("attrValueSelectedList", pmsSearchCrumbs);
    }
    modelMap.put("attrList",pmsBaseAttrInfos);
    String urlParam = getUrlParam(searchSkuParam,null);
    modelMap.put("urlParam",urlParam);

    String keyword = searchSkuParam.getKeyword();
    if(StringUtils.isNotBlank(keyword)){
      modelMap.put("keyword",keyword);
    }

    return "list";
  }

  private String getUrlParam(PmsSearchSkuParam searchSkuParam,Integer delValueId) {
    Integer catalog3Id = searchSkuParam.getCatalog3Id();
    String keyword = searchSkuParam.getKeyword();
    Integer []valueIds = searchSkuParam.getValueId();

    String urlParamStr = "";
    if(StringUtils.isNotBlank(keyword)){
      urlParamStr +="&keyword="+keyword;
    }

    if(catalog3Id!=null){
      urlParamStr +="&catalog3Id="+catalog3Id;
    }

    if(valueIds!=null){
      for(Integer valueId : valueIds){
        if((delValueId!=null&& !valueId.equals(delValueId))||(delValueId==null)) {
          urlParamStr += "&valueId=" + valueId;
        }
      }
    }
    if("&".equals(urlParamStr.substring(0,1))){
      urlParamStr = urlParamStr.substring(1,urlParamStr.length());
    }
    return urlParamStr;
  }



  @RequestMapping(value="saveEs",produces = "application/json;charset=UTF-8")
  @ResponseBody
  public String saveToEs() throws Exception{
    JestClient jestClient = JedisClientUtil.getJestClient();

    //查询mysql
    List<PmsSkuInfo> skuInfoList = new ArrayList<>();
    skuInfoList = skuService.getAllSku(61);

    //转化为es数据结构
    List<PmsSearchSkuInfo> skuSearchInfoList = new ArrayList<>();
    for(PmsSkuInfo skuInfo:skuInfoList){
      PmsSearchSkuInfo searchSkuInfo = new PmsSearchSkuInfo();
      BeanUtils.copyProperties(searchSkuInfo,skuInfo);
      skuSearchInfoList.add(searchSkuInfo);
    }
    //导入es
    for(PmsSearchSkuInfo searchSkuInfo2:skuSearchInfoList) {
      Index put = new Index.Builder(searchSkuInfo2).index("wanggmall").type("skuinfo").id(String.valueOf(searchSkuInfo2.getId())).build();
      jestClient.execute(put);
    }
    return "success";
  }

  //复杂查询
  @RequestMapping(value = "testes",produces = "application/json;charset=UTF-8")
  @ResponseBody
  public List<PmsSearchSkuInfo> test() throws Exception{

    //jest的dsl工具
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

    //bool
    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    //filter
    TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId","39");
    boolQueryBuilder.filter(termQueryBuilder);
    TermQueryBuilder termQueryBuilder1 = new TermQueryBuilder("skuAttrValueList.valueId","43");
    boolQueryBuilder.filter(termQueryBuilder1);

    //must
    MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName","小米");
    boolQueryBuilder.must(matchQueryBuilder);

    //query
    searchSourceBuilder.query(boolQueryBuilder);

    //from
    searchSourceBuilder.from(0);

    //size
    searchSourceBuilder.size(20);

    //highlight
    searchSourceBuilder.highlighter(null);

    String dsl = searchSourceBuilder.toString();
    System.out.println(dsl);

    JestClient jestClient = JedisClientUtil.getJestClient();
    Search search = new Search.Builder(dsl).addIndex("wanggmall").addType("skuinfo").build();
    SearchResult result = jestClient.execute(search);
    List<SearchResult.Hit<PmsSearchSkuInfo,Void>> hits = result.getHits(PmsSearchSkuInfo.class);
    List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
    for(SearchResult.Hit<PmsSearchSkuInfo,Void> hit :hits){
      PmsSearchSkuInfo source = hit.source;
      pmsSearchSkuInfos.add(source);
    }
    return pmsSearchSkuInfos;
  }


}
