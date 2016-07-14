package business.jdbc;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;



public abstract class JdbcAccountsDBTemplate extends JdbcTemplateBase {    
    @Inject
    @Named("dataSourceAccounts")
    public void setDataSource(DataSource dataSource) {
    	jdbcTemplate = new JdbcTemplate(dataSource);
    }    
}
