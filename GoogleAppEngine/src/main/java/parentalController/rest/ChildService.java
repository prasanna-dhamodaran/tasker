package parentalController.rest;


import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import parentalController.model.Child;
import parentalController.model.Parent;
import parentalController.model.PMF;
import parentalController.model.Task;
import parentalController.utilities.Utilities;

import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*@Path("/child")*/
@Api(
    name = "child",
    version = "v1",
    description = "Child Service"
)
public class ChildService
{
    /*@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{parentId}/all")
    public Parent getAllChildTasks(@PathParam("parentId") String parentEmail)*/
    @ApiMethod(
            name = "parent.get",
            path = "{parentId}/all",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public Parent getAllChildTasks(@Named("parentId") String parentEmail)
    {
        PersistenceManager persistenceManager = PMF.get().getPersistenceManager();
        Parent parent = null;
        try
        {
            Key parentId = KeyFactory.createKey(Parent.class.getSimpleName(), parentEmail);
            parent = persistenceManager.getObjectById(Parent.class, parentId);
        }
        catch(Exception e)
        {
            System.out.println("Exception during persistence of Parent: " + e.getMessage());
        }
        finally
        {
            persistenceManager.close();
        }

        return parent;
    }


    // Get child
    /*@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{childId}")
    public Child getChild(@PathParam("childId") String childEmailId)*/
    @ApiMethod(
            name = "child.get",
            path = "{childId}",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public Child getChild(@Named("childId") String childEmailId)
    {
        PersistenceManager persistenceManager = PMF.get().getPersistenceManager();
        Child child = null;

        persistenceManager.currentTransaction().begin();

        Query getChild = persistenceManager.newQuery("select from "
                         + Child.class.getName()
                         + " where"
                         + " email == '" + childEmailId + "'");

        //Child tempChild = new Child();
        //tempChild.setEmail(childEmailId);
        List<Child> children = (List<Child>) getChild.execute();

        if(!children.isEmpty())
        {
            child = children.get(0);
        }

        persistenceManager.currentTransaction().commit();
        return child;
    }

    // Add child to parent
    /*@POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{parentId}/{childId}")
    public Boolean addChild( @PathParam("parentId") String parentEmailId,
                             @PathParam("childId") String childEmailId)*/
    @ApiMethod(
            path = "{parentId}/{childId}",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public Map insertChild( @Named("parentId") String parentEmailId,
                            @Named("childId") String childEmailId)
    {
        Map<String, Boolean> status = new HashMap<String, Boolean>();
        status.put("status", false);

        PersistenceManager persistenceManager = PMF.get().getPersistenceManager();

        Parent parent = Utilities.getParent(persistenceManager, parentEmailId);
        if(parent != null)
        {
            Child child = Utilities.getChild(persistenceManager, childEmailId);
            if(child != null)
            {
                parent.getChildren().add(child.getEmail());
                status.put("status", true);
            }
            else
            {
                System.err.println("Child not found for id: " + childEmailId);
            }
        }
        else
        {
            System.err.println("Parent not found for id: " + parentEmailId);
        }

        persistenceManager.close();
        return status;
    }

    // Create mock stub data
    /*@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/makestubs")
    public boolean createStubData()*/
    @ApiMethod(
            name = "map.list",
            path = "makestubs",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public Map createStubData()
    {
        PersistenceManager persistenceManager = PMF.get().getPersistenceManager();
        Map<String, Boolean> status = new HashMap<String, Boolean>();
        status.put("status", false);

        try
        {
            // Create mock parent
            Key parentId = KeyFactory.createKey(Parent.class.getSimpleName(), "parent@gmail.com");
            Parent parent = new Parent();
            parent.setParentId(parentId);
            parent.setFirstName("Responsible");
            parent.setLastName("Parent");
            parent.setEmail("parent@gmail.com");

            // Create mock child
            Key childId = KeyFactory.createKey(Child.class.getSimpleName(), "child@gmail.com");
            Child child = new Child();
            child.setChildId(childId);
            child.setFirstName("Small");
            child.setLastName("Kid");
            child.setEmail("child@gmail.com");

            // Add one sample task for the child
            Task task = new Task();
            task.setTaskDescription("Finish homework today");
            task.setStatus(false);
            task.setPoints(5);

            // Add task to child
            child.addTask(task);

            // Add child to parent
            parent.getChildren().add(child.getEmail());

            persistenceManager.makePersistent(parent);
            persistenceManager.makePersistent(child);
            persistenceManager.makePersistent(task);

        }
        catch(Exception e)
        {
            System.out.println("Exception during persistence of stubs: " + e.getMessage() + " " + e.getLocalizedMessage());
            e.printStackTrace();
            return status;
        }
        finally
        {
            persistenceManager.close();
        }

        status.put("status", true);
        return status;
    }
}
