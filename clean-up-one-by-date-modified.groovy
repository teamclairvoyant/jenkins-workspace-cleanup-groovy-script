import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

//manager.listener.logger.println new Date(System.currentTimeMillis()).format('MM/dd/yyyy hh:mm:ss a') + " / " + " -- Start Time" 

//Get value from String Parameter
MAX_BUILDS = manager.build.buildVariables.get("MAX_BUILDS").toInteger()

parent_job_name = "";

// Finds the parent/cause job name
for (cause in manager.build.causes)
{
    if (cause.class.toString().contains("UpstreamCause")) {
         manager.listener.logger.println " ____ Parent Job Name : " + cause.getUpstreamProject()
         parent_job_name = cause.getUpstreamProject()
     }
}

manager.listener.logger.println "Current Job Name -"+ manager.build.project.getName()

manager.addShortText(parent_job_name, "#FFF", "#000", "1", "#FF0000") 

for (job in Jenkins.instance.items) 
{
  
  	int count = 0
  	
    manager.listener.logger.println "\n ***Job Name: "+job.name+"***"
    
    if(parent_job_name!="" && job.name==parent_job_name)
    {
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
    else
    {
        manager.listener.logger.print "--Skipped"
    }
}

//manager.listener.logger.println new Date(System.currentTimeMillis()).format('MM/dd/yyyy hh:mm:ss a') + " / " + " -- End Time"