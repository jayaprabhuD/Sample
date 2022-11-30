def call(Map pipelineParams)
{
	pipeline
	{
		agent
  		{
    		label 'Linux'
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
          		bitbucket_repo = "gal-cashless"
		        branch_type = 'branch_type'
          		branch = 'branch'
      		}
		stages
  		{
          		stage('Get Branch Type')
      			{
        			steps
          			{
        				sh 'git branch -r >branch1.txt'
		                  	sh 'cat branch1.txt'
              				sh 'cat branch1.txt | awk -F\'/\' \'{print $2}\' >branch_type.txt'
		                  	sh 'cat branch1.txt | awk -F\'/\' \'{print $3}\' >branch.txt'
                  	
              				script
              				{
		              			branch_type = readFile('branch_type.txt').trim()
                		  		echo "Branch Type is #${branch_type}#"
			                      	branch = readFile('branch.txt').trim()
                      				echo "Branch name is #${branch}#"
		                	}
            			}
       			}
    			stage("Build and Package")
      			{
        			steps
          			{
		              		echo "Branch Name is : ${env.BRANCH_NAME}"
                		  	echo "Branch Type is : ${branch_type}"
		                  	echo "Branch name is : ${branch}"
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