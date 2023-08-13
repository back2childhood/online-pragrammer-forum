package com.nowcoder.mycommunity.actuator;

import com.nowcoder.mycommunity.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;

    // readOperation means this method can only be accessed by get request
    // writeOperation means this method can only be accessed by post request
    @ReadOperation
    public String checkConnection(){
        try(
                Connection connection = dataSource.getConnection();
           ) {
            return CommunityUtil.getJSONString(0,"success");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("failed");
        }
        return CommunityUtil.getJSONString(1, "failed");
    }
}
