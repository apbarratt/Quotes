import java.util.ArrayList;
import java.util.List;

import me.prettyprint.cassandra.model.BasicColumnFamilyDefinition;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ThriftCfDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

/**
 * Class simply for the getting of the connection settings.
 * @author apbarratt
 *
 */
public class Connection {
	
	/**
	 * @return The cluster
	 */
	public static Cluster getCluster()
	{
		return HFactory.getOrCreateCluster("Quotes", new CassandraHostConfigurator("localhost:9160"));
	}
	
	//------------------------
	
	public static Keyspace getKeyspace()
	{
		SetUpKeySpaces(getCluster());
		return HFactory.createKeyspace("Quotes", getCluster());
	}
	
	public static void SetUpKeySpaces(Cluster c){
		try{
			try{
				@SuppressWarnings("unused")
				KeyspaceDefinition kd =c.describeKeyspace("Quotes");
			}catch(Exception et){
				System.out.println("Keyspace probably doesn't exist, trying to create it" + et);
				List<ColumnFamilyDefinition> cfs = new ArrayList<ColumnFamilyDefinition>();
				BasicColumnFamilyDefinition cf = new BasicColumnFamilyDefinition();

				//Setup Column Families in keyspace.
				
				cf.setName("Users");
				cf.setKeyspaceName("Quotes");
				cf.setComparatorType(ComparatorType.BYTESTYPE);
				ColumnFamilyDefinition cfDef = new ThriftCfDef(cf);
				cfs.add(cfDef);
				
				cf.setName("Subscriptions");
				cfDef = new ThriftCfDef(cf);
				cfs.add(cfDef);
				
				cf.setName("UserTweets");
				cfDef = new ThriftCfDef(cf);
				cfs.add(cfDef);
				
				cf.setName("Followers");
				cfDef = new ThriftCfDef(cf);
				cfs.add(cfDef);
				
				cf.setName("Tweets");
				cfDef = new ThriftCfDef(cf);
				cfs.add(cfDef);

				KeyspaceDefinition ks=HFactory.createKeyspaceDefinition("Quotes","org.apache.cassandra.locator.SimpleStrategy", 1, cfs);
				c.addKeyspace(ks);
			}


		}catch(Exception et){
			System.out.println("Other keyspace or column definition error" +et);
		}

	}
}
