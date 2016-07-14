package business.jdbc;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcProductsDBTemplate extends JdbcTemplateBase {
	@Inject
	@Named("dataSourceProducts")
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
