def call(Map pipelineParams)
{
	pipeline
	{
		agent
  		{
    		label 'Rapyder'
	    	}
    		options
  		{
    		buildDiscarder(
            	logRotator(
                	daysToKeepStr: '60',   // Build Records
                	artifactDaysToKeepStr: '60'  //Artifacts from builds older than this number of days will be deleted, but the logs, history, reports, etc for the build will be kept
            		)
        		)
        	timeout(50)
        	timestamps()
        	disableConcurrentBuilds()
    		}
  		environment 
		{ 
    			projectArtifactId = 'ArtifactId'
		    	projectGroupId = 'GroupId'
    			projectVersion = 'Version'
		     	artifactType = 'Packaging'
      		}
		stages
  		{    			
			stage("Build and Package")
      			{
        			steps
          			{
		              		echo "Branch Name is : ${env.BRANCH_NAME}"
                		  	echo (pipelineParams.nexus_url)
		                 	script
					{
						pom = readMavenPom file: "pom.xml"
                			    	projectArtifactId = pom.getArtifactId()
						projectGroupId = pom.getGroupId()
						projectVersion = pom.getVersion()
						artifactType = pom.getPackaging()
                			}
		              		sh "mvn clean install"
		                  	echo 'Build completed'
            			}	
       			}
		}
	}
}
