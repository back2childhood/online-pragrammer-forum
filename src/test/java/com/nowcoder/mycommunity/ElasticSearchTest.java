package com.nowcoder.mycommunity;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.CreateRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.nowcoder.mycommunity.dao.DiscussPostMapper;
import com.nowcoder.mycommunity.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.mycommunity.entity.DiscussPost;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MyCommunityApplication.class)
public class ElasticSearchTest {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

//    private ReactiveElasticsearchClient

    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList(){
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
    }

    @Test
    public void testUpdate(){
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("Hello, world!");
        discussPostRepository.save(post);
    }

    @Test
    public void testDelete(){
        discussPostRepository.deleteById(231);
        // discussPostRepository.deleteAll();
    }

    @Test
    public void testSearchRepository() throws IOException {


        // 部署ES的ip地址和端口
        RestClient restClient = RestClient.builder(
                new HttpHost("127.0.0.1", 9200)).build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        ElasticsearchClient client = new ElasticsearchClient(transport);

        List<Object> resultList = new ArrayList<>();

        // 索引
        String index = "discusspost";
        // 索引别名
        String aliases = "discusspost-01";
//        /**
//         * 创建索引
//         * @param index: 索引名称
//         * @param aliases: 别名
//         * @author : XI.QING
//         * @date : 2021/12/28
//         */
//        client.indices().create(c -> c
//                .index(index)
//                .aliases(aliases, a -> a
//                        .isWriteIndex(true)));
//        /**
//         * 创建数据文档
//         * @param index: 索引名称
//         * @author : XI.QING
//         * @date : 2021/12/29
//         */
//        Map<String, String> map = new HashMap<>();
//        map.put("username", "张三");
//        map.put("address", "江苏省南京市");
//        CreateRequest dataStreamResponse = CreateRequest.of(e -> e
//                .index(index)
//                .id("1")
//                .type("_doc")
//                .document(map));
//        client.create(dataStreamResponse);

        /**
         * 查询索引
         * @param indexList: 查询索引的名称
         * @param clazz: 返回结果的类型
         */
        // Object是一个po实例,如自定义的User、Book、Student等等
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .bool(b -> b
                                .must(must -> must
                                        .match(m -> m
                                                .field("title")
                                                .query("互联网")
                                        )
                                )
                                .must(must -> must
                                        .match(m -> m
                                                 .field("content")
                                                 .query("互联网")
                                        )
                                )
                        )
                )
                .sort(sort -> sort
                        .field(f -> f
                                .field("type")
                                .order(SortOrder.Desc)
                        )
                )
                .sort(sort -> sort
                        .field(f -> f
                                .field("score")
                                .order(SortOrder.Desc)
                        )
                )
                .sort(sort -> sort
                        .field(f -> f
                                .field("createTime")
                                .order(SortOrder.Desc)
                        )
                )
                .from(0)
                .size(10)
                .highlight(h -> h
                        .fields("title", f -> f
                                .preTags("<em>")
                                .postTags("</em>")
                        )
                        .fields("content", f -> f
                                .preTags("<em>")
                                .postTags("</em>")
                        )
                )
        );
        SearchResponse<DiscussPost> response = client.search(searchRequest, DiscussPost.class);
//        SearchPage<DiscussPost> page = SearchHitSupport.searchPageFor(response.hits(), );
        System.out.println(response.hits().hits().size());
        if (response.hits() != null) {
            List<Hit<DiscussPost>> list = response.hits().hits();
            for (Hit<DiscussPost> hit : list) {
                Object t = (Object) hit.source();
                resultList.add(t);
            }
        }
//        /**
//         * 删除索引
//         * @param index: 索引名称
//         */
//        // 删除索引(范围大)
//        client.delete(c -> c.index(index));
//        // 删除索引和ID(范围小)
//        client.delete(c -> c.index(index).id("1"));


        // 关闭客户端连接部分
        transport.close();
        restClient.close();
    }
}
