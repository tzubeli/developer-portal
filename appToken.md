# Application Token Authorization Workflow

An Application Token is useful in cases where different applications and users with varying access permissions need to access your Kaltura account via API. 
The appToken is created and customized by the account administrator, and then used by the developers to generate Kaltura Sessions for their respective applications.

## Before You Start

Before you create an appToken, you need to determine two things: which content to provide access to, and which actions to grant permissions for. 

### Content Access
Access to content is determined by the User that is included in the AppToken creation. That user is added via the KMC or directly through the API to the relevant categories that should be available to the KS. 

### Action Permissions 
Also included in the AppToken creation is the **Privileges String** which is made up of key-value pairs that determine the actions available to the session, most importantly, the `setrole` key. It is mapped to the ID of the role, which you'll create with a list of the API actions permitted to this KS. 


## Creating the AppToken 

Let's walk through the steps mentioned above: 

### Create a Kaltura User

Very simply, a new user requires an ID (string) that will identify it. The most basic code for the `user.add` action will look something like this: 

```
user = KalturaUser()
user.id = "DummyUser@kaltura.com"

result = client.user.add(user);
print(result);
```

There are additional fields that can be associated with a user, which you can find by creating a user in the [interactive console](https://developer.kaltura.com/console/service/user/action/add). 
Now that you have your user, you can set its content access. 

### Add the User to a Category 

### Create a User Role: KMC 
The simpler way to create a User Role is in the Kaltura KMC. Under administration settings (the person icon), click the Roles tab and hit “Add Role” 
You’ll have options to name and describe the new role (make it specific) and then select permitted actions. You’ll see that for each action, there is the option to allow all options, or to select specific permissions under that category. For example, under Content Moderation, you may allow this User Role to perform all actions except for deleting. You can also switch off a specific action altogether. Hit save and you should now see your new user role in the list. 

### Create a User Role: API 

Alternatively, if you know exactly which actions you’d like to include in your User Role (you can see all their names and descriptions in permission.list), you can use the userrole.add API action to create a new role.

Give your new role a name and a description, add permissions, and set the status to active (1). 

--- code sample ---


*Note that you will not be able to see in the KMC any roles that are created outside the KMC.* You can see a list of all  your existing roles, however, with the userrole.list action. Make note of the ID of your new userrole as you’ll be needing it for your new apptoken. 

### Generate a Kaltura Session with the App Token 
