package parentalController.rest;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import parentalController.model.Child;
import parentalController.model.Credentials;
import parentalController.model.PMF;
import parentalController.model.Parent;

import javax.jdo.PersistenceManager;
import java.util.HashMap;
import java.util.Map;

//@Path("/auth")
@Api(
    name = "auth",
    version = "v1",
    description = "Authentication Service"
)
public class AuthenticationService
{
    @ApiMethod(
            name = "map.login",
            path = "login",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public Map login(Credentials credentials)
    {
        Map<String, Boolean> status = new HashMap<String, Boolean>();
        status.put("status", false);

        if(credentials == null)
        {
            status.put("status", false);
            return status;
        }

        // Issue login condition here
        PersistenceManager persistenceManager = PMF.get().getPersistenceManager();
        Key userKey = null;
        status.put("status", false);

        if(credentials.getUserType().equalsIgnoreCase("parent"))
        {
            userKey = KeyFactory.createKey(Parent.class.getSimpleName(), credentials.getUserId());
            Parent parent = null;
            try
            {
                parent = persistenceManager.getObjectById(Parent.class, userKey);
                if(parent != null)
                {
                    status.put("status", true);
                }
            }
            catch (Exception e) // Parent not in database, so create a new one
            {
                // Parent does not exists, add the parent to the Datastore
                parent = new Parent();
                parent.setParentId(userKey);
                parent.setEmail(credentials.getUserId());
                parent.setToken(credentials.getToken());
                persistenceManager.makePersistent(parent);

                status.put("status", true);
            }
        }
        else if(credentials.getUserType().equalsIgnoreCase("child"))
        {
            userKey = KeyFactory.createKey(Child.class.getSimpleName(), credentials.getUserId());
            Child child = null;
            try
            {
                child = persistenceManager.getObjectById(Child.class, userKey);
                if(child != null)
                {
                    status.put("status", true);
                }
            }
            catch (Exception e)
            {
                // Child does not exists, add the child to the Datastore
                child = new Child();
                child.setChildId(userKey);
                child.setEmail(credentials.getUserId());
                child.setToken(credentials.getToken());
                persistenceManager.makePersistentAll(child);

                status.put("status", true);
            }
        }

        persistenceManager.close();
        return status;

    }

    @ApiMethod(
            name = "map.logout",
            path = "logout",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public Map logout(Credentials credentials)
    {
        Map<String, Boolean> status = new HashMap<String, Boolean>();
        status.put("status", false);

        if(credentials == null)
        {
            status.put("status", false);
            return status;
        }

        // Issue logout condition here
        PersistenceManager persistenceManager = PMF.get().getPersistenceManager();
        Key userKey = null;

        if(credentials.getUserType().equalsIgnoreCase("parent"))
        {
            userKey = KeyFactory.createKey(Parent.class.getSimpleName(), credentials.getUserId());
            Parent parent = null;
            try
            {
                parent = persistenceManager.getObjectById(Parent.class, userKey);
            }
            catch (Exception e)
            {
                // Parent not found in database, so dont do anything and send false back
                status.put("status", false);
            }

            if(parent != null)
            {
                parent.setToken("");
                status.put("status", true);
            }

        }
        else if(credentials.getUserType().equalsIgnoreCase("child"))
        {
            userKey = KeyFactory.createKey(Child.class.getSimpleName(), credentials.getUserId());
            Child child = null;
            try
            {
                child = persistenceManager.getObjectById(Child.class, userKey);
                if(child != null)
                {
                    child.setToken("");
                    status.put("status", true);
                }
            }
            catch (Exception e)
            {
                // Child not found in database, so dont do anything and send false back
                status.put("status", false);
            }
        }

        persistenceManager.close();
        return status;
    }

}
