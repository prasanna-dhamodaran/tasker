package parentalController.rest;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import parentalController.model.Child;
import parentalController.model.PMF;
import parentalController.model.Task;
import parentalController.utilities.Utilities;

import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api(
    name = "task",
    version = "v1",
    description = "Task Service"
)
public class TaskService
{
    // Get Tasks for a Child
    @ApiMethod(
            name = "set.childTaskList",
            path = "{childId}",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public Set<Task> getChildTaskList(@Named("childId") String childEmail)
    {
        PersistenceManager persistenceManager = PMF.get().getPersistenceManager();
        Child child = null;

        try
        {
            /*Key childId = new KeyFactory
                          .Builder(Parent.class.getSimpleName(), parentEmail)
                          .addChild(Child.class.getSimpleName(), childEmail)
                          .getKey();
            child = persistenceManager.getObjectById(Child.class, childId);*/

            // Get child based on child's email id
            child = Utilities.getChild(persistenceManager, childEmail);

        }
        catch(Exception e)
        {
            System.out.println("Exception during persistence of Parent: " + e.getMessage());
        }
        finally
        {
            persistenceManager.close();
        }

        if(child != null)
        {
            return child.getTasks();
        }
        else
        {
            return null;
        }
    }

    // Add Task for a child
    @ApiMethod(
            name = "map.addTask",
            path = "{childId}",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public Map addChildTask( @Named("childId") String childEmail,
                             Task task)
    {
        PersistenceManager persistenceManager = PMF.get().getPersistenceManager();
        String taskId = "";
        Child child = null;
        Map<String, String> status = new HashMap<String, String>();
        status.put("taskId", "");

        try
        {
            Task newTask = new Task();
            newTask.setTaskDescription(task.getTaskDescription());
            newTask.setPoints(task.getPoints());
            newTask.setStatus(task.getStatus());

            // Fetch the child matching the given email id
            persistenceManager.currentTransaction().begin();

            // Form query for fetching the child
            Query getChild = persistenceManager.newQuery("select from "
                    + Child.class.getName()
                    + " where"
                    + " email == '" + childEmail + "'");

            child = new Child();
            child.setEmail(childEmail);
            List<Child> allMatchingChildren = (List<Child>) getChild.execute();
            // Complete the transaction pertaining to Child fetch
            persistenceManager.currentTransaction().commit();

            if(!allMatchingChildren.isEmpty())
            {
                child = allMatchingChildren.get(0);
                child.addTask(newTask);

                // Save the new task to the datastore
                persistenceManager.makePersistent(newTask);
                taskId = newTask.getTaskId().toString();
                Pattern p = Pattern.compile("\\(([0-9]+).*");
                Matcher m = p.matcher(taskId);

                if(m.find())
                {
                    taskId = m.group(1);
                }
            }
            else
            {
                taskId = "";
                System.err.println("No matching child found for: " + childEmail);
            }
        }
        catch(Exception e)
        {
            System.err.println("Exception during addChildTask");
        }
        finally
        {
            persistenceManager.close();
        }

        status.put("taskId", taskId);
        return status;
    }

    // Update Task status for a child
    @ApiMethod(
            name = "map.updateTask",
            path = "{childId}/{taskId}",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public Map changeTaskStatus( @Named("childId") String childEmail,
                                 @Named("taskId") String taskId,
                                 Task receivedTask)
    {
        PersistenceManager persistenceManager = PMF.get().getPersistenceManager();
        Map<String, Boolean> status = new HashMap<String, Boolean>();
        status.put("status", false);

        try
        {
            /*Key taskIdentity = new KeyFactory
                    .Builder(Parent.class.getSimpleName(), parentEmail)
                    .addChild(Child.class.getSimpleName(), childEmail)
                    .addChild(Task.class.getSimpleName(), Long.parseLong(taskId))
                    .getKey();*/

            //Child child = getChild(persistenceManager, childEmail);  // Check if needed
            Key taskIdentity = new KeyFactory
                    .Builder(Child.class.getSimpleName(), childEmail)
                    .addChild(Task.class.getSimpleName(), Long.parseLong(taskId))
                    .getKey();

            Task task = persistenceManager.getObjectById(Task.class, taskIdentity);
            task.setStatus(receivedTask.getStatus());
            status.put("status", true);
        }
        catch(Exception e)
        {
            System.out.println("Exception during persistence of Parent");
        }
        finally
        {
            persistenceManager.close();
        }
        return status;
    }

}
