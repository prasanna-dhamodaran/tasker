package parentalController.utilities;

import parentalController.model.Child;
import parentalController.model.Parent;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.List;

public class Utilities
{
    // Fetch child object from Datastore based on email id
    public static Child getChild(PersistenceManager persistenceManager, String childEmailId)
    {
        persistenceManager.currentTransaction().begin();

        // Form query for fetching the child
        Query getChild = persistenceManager.newQuery("select from "
                + Child.class.getName()
                + " where"
                + " email == '" + childEmailId + "'");

        Child child = new Child();
        child.setEmail(childEmailId);
        List<Child> allMatchingChildren = (List<Child>) getChild.execute();

        if(!allMatchingChildren.isEmpty())
        {
            child = allMatchingChildren.get(0);

            // Complete the transaction pertaining to Child fetch
            persistenceManager.currentTransaction().commit();
            return child;
        }
        else
        {
            System.err.println("No matching child found for: " + childEmailId);
            return null;
        }
    }

    public static Parent getParent(PersistenceManager persistenceManager, String parentEmailId)
    {
        persistenceManager.currentTransaction().begin();

        // Form query for fetching the child
        Query getParentQuery = persistenceManager.newQuery("select from "
                + Parent.class.getName()
                + " where"
                + " email == '" + parentEmailId + "'");

        Parent parent = new Parent();
        parent.setEmail(parentEmailId);
        List<Parent> allMatchingParents = (List<Parent>) getParentQuery.execute();

        if(!allMatchingParents.isEmpty())
        {
            parent = allMatchingParents.get(0);

            // Complete the transaction pertaining to Child fetch
            persistenceManager.currentTransaction().commit();
            return parent;
        }
        else
        {
            System.err.println("No matching parent found for: " + parentEmailId);
            return null;
        }
    }

}
