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

Let's walk through the steps to creating an App Token

### Step 1: User Content Access

We'll need to give access for specific content to a specific user. If you don't have a user yet, you can create one easily using the [`user.add`](https://developer.kaltura.com/console/service/user/action/add) action. 

```
user = KalturaUser()
user.id = "DummyUser@kaltura.com"

result = client.user.add(user);
print(result);
```
Additional user details can be found in the [console](https://developer.kaltura.com/console/service/user/action/add). That ID will be used for granting access, which you can do easily in the KMC. 

1. You'll need to first add entitlements to the category in question by going to Settings > Integration Settings and selecting Add Entitlement. You'll be given the option to select one of your existing categories. (Edit: comment on privacy context?)

2. Back in Content > Categories, when you edit the category, you'll now be able to see the Entitlements tab. 

.... 
You can also do this via the api with the `categoryUser.add` action


### Step 2: Create a user role 
Again, the simple way to create a user role is via the KMC, under administration (the icon of a person) and Roles > Add Role. 
You’ll have options to name and describe the new role (make it specific) and then select permitted actions. You’ll see that for each action, there is the option to allow all options, or to select specific permissions under that category. For example, under Content Moderation, you may allow this User Role to perform all actions except for deleting. You can also switch off a specific action altogether. Hit save and you should now see your new user role in the list. 

Alternatively, if you know exactly which actions you’d like to include in your User Role (you can see all their names and descriptions in `permission.list`), you can use the `userRole.add` API action to create a new role. Be sure to set the status of your role to Active (1) 

*Note that you will not be able to see in the KMC any roles that are created outside the KMC.* 
You can see a list of all  your existing roles, however, with the `userRole.list` action. Make note of the ID of your new user role as you’ll be needing it for your appToken. 

### Generate a Kaltura Session with the App Token 
