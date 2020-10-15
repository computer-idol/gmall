package com.wang.gmall.list.service;

import com.wang.gmall.bean.PmsSearchSkuInfo;
import com.wang.gmall.bean.PmsSearchSkuParam;
import com.wang.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

  public static JestClient getJestClient(){
    JestClientFactory factory = new JestClientFactory();
    factory.setHttpClientConfig(new HttpClientConfig
            .Builder("http://47.112.214.8:9200")
            .multiThreaded(true)
            .build());
    return  factory.getObject();
  }

  @Override
  public List<PmsSearchSkuInfo> list(PmsSearchSkuParam searchSkuParam) throws Exception{
    String dsl = getDsl(searchSkuParam);
    System.out.println(dsl);

    JestClient jestClient = getJestClient();
    Search search = new Search.Builder(dsl).addIndex("wanggmall").addType("skuinfo").build();
    SearchResult result = jestClient.execute(search);
    List<SearchResult.Hit<PmsSearchSkuInfo,Void>> hits = result.getHits(PmsSearchSkuInfo.class);
    List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
    for(SearchResult.Hit<PmsSearchSkuInfo,Void> hit :hits){
      PmsSearchSkuInfo source = hit.source;
      Map<String,List<String>> hightlight = hit.highlight;
      if(hightlight!=null) {
        String skuName = hightlight.get("skuName").get(0);
        source.setSkuName(skuName);
      }
      pmsSearchSkuInfos.add(source);
    }
    return pmsSearchSkuInfos;

  }

  String getDsl(PmsSearchSkuParam searchSkuParam){

    //List<PmsSkuAttrValue> skuAttrValueList = searchSkuParam.getPmsSkuAttrValues();
    Integer []valueIds = searchSkuParam.getValueId();
    String keyword = searchSkuParam.getKeyword();
    //jest的dsl工具
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    Integer catalog3Id = searchSkuParam.getCatalog3Id();

    //bool
    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    //filter
    if(catalog3Id!=null){
      TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",String.valueOf(catalog3Id));
      boolQueryBuilder.filter(termQueryBuilder);
    }
//    if(skuAttrValueList!=null){
//      for(PmsSkuAttrValue skuAttrValue:skuAttrValueList){
//        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",skuAttrValue.getValueId());
//        boolQueryBuilder.filter(termQueryBuilder);
//      }
//    }

    if(valueIds!=null){
      for(Integer valueId : valueIds ){
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",valueId);
        boolQueryBuilder.filter(termQueryBuilder);
      }
    }

    //must
    if(StringUtils.isNotBlank(keyword)) {
      MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
      boolQueryBuilder.must(matchQueryBuilder);
    }

    //query
    searchSourceBuilder.query(boolQueryBuilder);

    //from
    searchSourceBuilder.from(0);

    //size
    searchSourceBuilder.size(20);

    //highlight(高亮加红)
    HighlightBuilder highlightBuilder = new HighlightBuilder();
    highlightBuilder.preTags("<span style='color:red'>");
    highlightBuilder.field("skuName");
    highlightBuilder.postTags("</span>");
    searchSourceBuilder.highlighter(highlightBuilder);

    //sort(倒序排)
    searchSourceBuilder.sort("id",SortOrder.DESC);

//    //aggression聚合查询,获取属性列表
//    TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
//    searchSourceBuilder.aggregation(termsAggregationBuilder);
    return searchSourceBuilder.toString();

  }

}
