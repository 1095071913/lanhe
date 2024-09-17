/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.maozi.generate.code.parent.enums;

import com.maozi.generate.code.tool.SQLType;



public class GenerateEnumPom {
	
	public static void generate(String module,String pash) throws Exception {
		
		String context = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n" + 
				"  <modelVersion>4.0.0</modelVersion>\r\n" + 
				"  <parent>\r\n" + 
				"    <groupId>com.maozi</groupId>\r\n" + 
				"    <artifactId>maozi-cloud-"+module+"</artifactId>\r\n" + 
				"    <version>1.0.0-SNAPSHOT</version>\r\n" + 
				"  </parent>\r\n" + 
				"  <artifactId>maozi-cloud-"+module+"-enum</artifactId>\r\n" + 
				"  \r\n" + 
				"  	<dependencies>\r\n" + 
				"\r\n" + 
				"		<!-- Project Begin -->\r\n" + 
				"		<dependency>\r\n" + 
				"			<groupId>com.maozi</groupId>\r\n" + 
				"			<artifactId>maozi-cloud-enum</artifactId>\r\n" + 
				"			<version>${maozi-cloud-enum.version}</version>\r\n" + 
				"			<type>pom</type>\r\n" + 
				"		</dependency>  \r\n" + 
				"		<!-- Project Begin -->   \r\n" + 
				"\r\n" + 
				"	</dependencies>\r\n" + 
				"  \r\n" + 
				"</project>";
		
		SQLType.fileCreate(pash, "pom", new StringBuilder(context),"xml");
		
	}

}
