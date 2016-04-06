package rentit.com.infrastructure;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.ObjectNameNormalizer;
import org.hibernate.cfg.naming.NamingStrategyDelegator;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("deprecation")
@Service
public class HibernateBasedIdentifierGenerator {
	private final SessionFactory sessionFactory;
	private final Dialect dialect;
	private final Map<String, SequenceGenerator> generators = new HashMap<>();
	private final ObjectNameNormalizer nameNormalizer;

	@Autowired
	public HibernateBasedIdentifierGenerator(EntityManagerFactory emf) throws SQLException {
		sessionFactory =  emf.unwrap(SessionFactory.class);
		dialect = ((SessionFactoryImpl)sessionFactory).getDialect();

		nameNormalizer = new ObjectNameNormalizer() {
			Configuration conf = new Configuration();
			protected boolean isUseQuotedIdentifiersGlobally() { return false; }
			protected NamingStrategyDelegator getNamingStrategyDelegator() { return conf.getNamingStrategyDelegator(); }
			protected NamingStrategy getNamingStrategy() { return conf.getNamingStrategy(); }
		};
	}

	public Long getID(final String sequenceName) {
		SequenceGenerator generator = generators.get(sequenceName);
		if (generator == null) {
			Properties params = new Properties();
	        params.setProperty("sequence", sequenceName);
	        params.put("identifier_normalizer", nameNormalizer);
	        generator = new SequenceGenerator();
	        generator.configure(LongType.INSTANCE, params, dialect);
	        generators.put(sequenceName, generator);
		}
		return (Long) generator.generate((SessionImpl)sessionFactory.openSession(), null);
	}
}

