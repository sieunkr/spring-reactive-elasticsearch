package com.example.demo.provider;

import com.example.demo.core.Coffee;
import com.example.demo.core.CoffeeUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
public class ElasticsearchProvider implements CoffeeUseCase {

    private final RestHighLevelClient restHighLevelClient;

    public ElasticsearchProvider(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }


    @Override
    public Flux<Coffee> findAll() {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.query(QueryBuilders.typeQuery("coffee"));

        return getCoffeeFlux(searchSourceBuilder);
    }

    @Override
    public Flux<Coffee> findByTitle(String title) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("title", title));

        return getCoffeeFlux(searchSourceBuilder);
    }

    @Override
    public Flux<Coffee> matchPhraseQueryByTitle(String title) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("title", title));

        return getCoffeeFlux(searchSourceBuilder);
    }


    private Flux<Coffee> getCoffeeFlux(SearchSourceBuilder searchSourceBuilder) {
        SearchRequest searchRequest = new SearchRequest("cafe"); //index
        searchRequest.source(searchSourceBuilder);

        return Flux.<Coffee>create(sink -> {
            ActionListener<SearchResponse> actionListener = new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {

                    for(SearchHit hit : searchResponse.getHits()) {
                        ObjectMapper objectMapper = new ObjectMapper();

                        try {
                            Coffee coffee = objectMapper.readValue(hit.getSourceAsString(), Coffee.class);
                            sink.next(coffee);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    sink.complete();
                }

                @Override
                public void onFailure(Exception e) {
                }
            };
            restHighLevelClient.searchAsync(searchRequest, RequestOptions.DEFAULT, actionListener);
        });
    }


    @Override
    public Mono<Coffee> findById(String id) {

        GetRequest getRequest = new GetRequest(
                "cafe",
                "coffee",
                id);

        //TODO: 구현
        return null;
    }



    /*
    @Override
    public Flux<Coffee> findByTitle(String title) {

        GetRequest getRequest = new GetRequest(
                "cafe",
                "coffee",
                "1");

        return Flux.<Coffee>create(sink -> {

            ActionListener<GetResponse> actionListener = new ActionListener<GetResponse>() {
                @Override
                public void onResponse(GetResponse documentFields) {

                    System.out.println("테스트");

                    documentFields.

                    //sink.next("dd");
                    sink.complete();
                }
                @Override
                public void onFailure(Exception e) {
                }
            };

            restHighLevelClient.getAsync(getRequest, RequestOptions.DEFAULT, actionListener);
        });

    }
    */
}
