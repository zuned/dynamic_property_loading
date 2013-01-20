dynamic_property_loading
========================

Re Loadable properties 

1. Spring 3.1.2 RELEASE , google guava , Maven 3


FAQS :
Q  : How to build?
Ans:
1. build pom.xml
2. build dynamic_resource_load/pom.xml
3. build resource_updator/pom.xml


Q   : How to deploy ?
Ans : Provide user_config_path path in your environment variable

In tomcat you can do context setting like as below
<Context docBase="C:/Users/admin/Documents/workspace-sts/spring_learn/dynamic_resource_load/target/learn-0.0.1-SNAPSHOT" >
	<Environment name="user_config_path" override="false" type="java.lang.String" value="C:/" />
</Context>

