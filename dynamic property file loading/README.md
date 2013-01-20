dynamic_property_loading
========================

Re Loadable properties 

1. Spring 3.1.2 RELEASE , google guava , Maven 3
<br><br>

<b>FAQS :</b>

<b>Q  : How to build?</b><br>
<b>Ans:<b>
<ol>
	<li> build pom.xml </li>
	<li> build dynamic_resource_load/pom.xml </li>
	<li> build resource_updator/pom.xml </li>
  </ol>
<br><br>


<b>Q   : How to deploy ?</b>
<b>Ans :</b> Provide user_config_path path in your environment variable
<br><br>
In tomcat you can do context setting like as below <br><br>
&lt; Context docBase="C:/Users/admin/Documents/workspace-sts/spring_learn/dynamic_resource_load/target/learn-0.0.1-SNAPSHOT" &gt; <br>
	&lt; Environment name="user_config_path" override="false" type="java.lang.String" value="C:/" /&gt; <br>
&lt; /Context &gt;

