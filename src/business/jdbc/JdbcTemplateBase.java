package business.jdbc;

import java.util.logging.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class JdbcTemplateBase{
	protected final Logger LOG = Logger.getLogger(getClass().getPackage().getName());	
	protected JdbcTemplate jdbcTemplate;
}
