package neo.ehsanodyssey.oauth.config.mongo;

import com.github.mongobee.Mongobee;
import neo.ehsanodyssey.oauth.config.mongo.changelogs.MongoChangeLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 16 Dec 2019
 */
@Configuration
@DependsOn("mongoTemplate")
public class MigrationConfig {

    @Value("${spring.data.mongodb.uri}")
    private String uri;
    @Value("${spring.data.mongodb.host}")
    private String host;
    @Value("${spring.data.mongodb.port}")
    private int port;
    @Value("${spring.data.mongodb.database}")
    private String database;
    @Value("${spring.data.mongodb.username}")
    private String username;
    @Value("${spring.data.mongodb.password}")
    private String password;

    private final MongoTemplate mongoTemplate;

    public MigrationConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Bean
    public Mongobee mongobee(final Environment environment) {
        Mongobee runner = new Mongobee(String.format(uri,
                host, port, database));
        runner.setMongoTemplate(mongoTemplate);
        runner.setDbName(database);
        runner.setChangeLogsScanPackage(MongoChangeLog.class.getPackageName());
        runner.setSpringEnvironment(environment);
        return runner;
    }
}
