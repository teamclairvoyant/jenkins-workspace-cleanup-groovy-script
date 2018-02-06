import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

//manager.listener.logger.println new Date(System.currentTimeMillis()).format('MM/dd/yyyy hh:mm:ss a') + " / " + " -- Start Time" 

//Get value from String Parameter
MAX_BUILDS = manager.build.buildVariables.get("MAX_BUILDS").toInteger()

for (job in Jenkins.instance.items) 
{
  
  	int count = 0
  	
    manager.listener.logger.println "\n ***Job Name: "+job.name+"***"
    
        if(job.workspace!=null && job.workspace!="")  //Check if there is a workspace associated with the Job
        {
            manager.listener.logger.println "Workspace path : " + job.workspace
            
            String workspace = job.workspace
            
            File folder = new File(workspace)
            
            if(folder!=null && folder.exists()) //Check if the Workspace folder exists
            {
                // Get all files and folders within the Workspace of current job. 
                //Iterate through only folders and sort em by Modified Date.
                
                File[] files = new File(workspace).listFiles().sort(){
                a,b -> b.lastModified().compareTo a.lastModified()
                }
                .each{
                    if(!it.isFile()) //Check only for folders
                    {
                        if(count < MAX_BUILDS)
                            manager.listener.logger.println new Date(it.lastModified()).format('MM/dd/yyyy hh:mm:ss a') + " /" + it.name + " -- Save" 
                        else
                        {
                            manager.listener.logger.println new Date(it.lastModified()).format('MM/dd/yyyy hh:mm:ss a') + " /" + it.name + " ** Deleted" 
                            it.deleteDir()
                        }
                        count++
                    }
                }
            }
            else
            {
                manager.listener.logger.println "Workspace is empty or doesn't exist"
            }
        }
        else
        {
            manager.listener.logger.println "No Workspace associated with this job"
        }
    }

}

//manager.listener.logger.println new Date(System.currentTimeMillis()).format('MM/dd/yyyy hh:mm:ss a') + " / " + " -- End Time" 