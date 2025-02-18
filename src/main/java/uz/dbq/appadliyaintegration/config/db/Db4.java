package uz.dbq.appadliyaintegration.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

@Configuration
@ConfigurationProperties("spring.datasource.data-base-fourth")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryDataBaseFourth",
        transactionManagerRef = "transactionManagerDataBaseFourth",
        basePackages = {"uz.dbq.appadliyaintegration.repository.repo4"}
)
public class Db4 {

    private static final String POOL_NAME = "dataBaseFourth";

    private static HikariDataSource hikariDataSource;

    @Value("${spring.datasource.data-base-fourth.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.data-base-fourth.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.data-base-fourth.username}")
    private String username;

    @Value("${spring.datasource.data-base-fourth.password}")
    private String password;

    protected final String PERSISTENCE_UNIT_NAME = "dataBaseFourth";
    protected final Properties JPA_DATABASE_FIRST = new Properties() {{
        put("database-platform", "org.hibernate.dialect.DB2400Dialect");
        put("hibernate.hbm2ddl.auto", "none");
        put("hibernate.dialect", "org.hibernate.dialect.DB2400Dialect");
        put("hibernate.show_sql", "true");
        put("generate-ddl", "true");
    }};

    public static Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @Bean
    @Qualifier("dataBaseFourth")
    public HikariDataSource dataBaseFourth() throws UnknownHostException, SocketException {
        if (hikariDataSource == null) {
            synchronized (Db4.class) {
                if (hikariDataSource == null) {
                    HikariConfig hikariConfig = createHikariConfig();
                    hikariDataSource = new HikariDataSource(hikariConfig);
                }
            }
        }
        return hikariDataSource;
    }

    private HikariConfig createHikariConfig() throws UnknownHostException, SocketException {
        HikariConfig hikariConfig = new HikariConfig();

        String ip;
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }

        setCommonHikariProperties(hikariConfig);

        if (isLocalhost(ip)) {
            setLocalhostHikariProperties(hikariConfig);
        } else {
            setRemoteHikariProperties(hikariConfig);
        }

        return hikariConfig;
    }

    private void setCommonHikariProperties(HikariConfig hikariConfig) {
        hikariConfig.setAutoCommit(true);
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        hikariConfig.setPoolName(POOL_NAME);
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setConnectionTestQuery("select current_timestamp cts from sysibm.sysdummy1");
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setValidationTimeout(5000);
    }

    private void setLocalhostHikariProperties(HikariConfig hikariConfig) {
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setIdleTimeout(500000);
        hikariConfig.setMaxLifetime(600000);
    }

    private void setRemoteHikariProperties(HikariConfig hikariConfig) {
        hikariConfig.setMinimumIdle(10);
        hikariConfig.setMaximumPoolSize(500);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(3600000);
    }

    private boolean isLocalhost(String ip) {
        return ip.equals("192.168.224.224") || ip.equals("192.168.224.18") ||
                ip.equals("192.168.224.127") || ip.equals("localhost") || ip.equals("192.168.224.13");
    }

    @Bean(name = "entityManagerFactoryDataBaseFourth")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryDataBaseFourth(
            final HikariDataSource dataBaseFourth) {
        return new LocalContainerEntityManagerFactoryBean() {{
            setDataSource(dataBaseFourth);
            setPersistenceProviderClass(HibernatePersistenceProvider.class);
            setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
            setPackagesToScan("uz.dbq.appadliyaintegration.entity.entity4");
            setJpaProperties(JPA_DATABASE_FIRST);
        }};
    }

    @Bean
    public PlatformTransactionManager transactionManagerDataBaseFourth(
            final @Qualifier("entityManagerFactoryDataBaseFourth") LocalContainerEntityManagerFactoryBean entityManagerFactoryDataBaseFourth) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryDataBaseFourth.getObject()));
    }
}



