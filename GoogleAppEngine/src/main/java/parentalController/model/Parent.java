package parentalController.model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import javax.jdo.annotations.*;
import java.util.HashSet;
import java.util.Set;


@PersistenceCapable
public class Parent
{
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key parentId;

    @Persistent
    private String email;

    @Persistent
    private String firstName;

    @Persistent
    private String lastName;

    @Persistent
    private String token;

    @Persistent
    private Set<String> children;

    public Parent()
    {
        this.children = new HashSet<String>();
    }

    public Key getParentId()
    {
        return parentId;
    }

    public void setParentId(Key parentId)
    {
        this.parentId = parentId;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public Set<String> getChildren()
    {
        return children;
    }

    public void setChildren(Set<String> children)
    {
        this.children = children;
    }

    public boolean addChild(String childEmail)
    {
        Set<String> children = this.getChildren();
        for (String individualChild : children)
        {
            if(individualChild.equalsIgnoreCase(childEmail))
            {
                return false;
            }
        }

        //Child child = new Child();
        //Key childId = KeyFactory.createKey(Child.class.getSimpleName(), childEmail);
        //child.setChildId(childId);
        //children.add(child);
        this.children.add(childEmail);

        return true;
    }
}
