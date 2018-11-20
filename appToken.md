# Application Token Authorization Workflow

An Application Token is useful in cases where different applications and users with varying access permissions need to access your Kaltura account via API. 
The appToken is created and customized by the account administrator, and then used by the developers to generate Kaltura Sessions for their respective applications.

## Before You Start

Before you create an appToken, you need to determine two things: 
1. Which content this application will need access to.
2. Which API actions this app will be granted permissions for. 

### Content Entitlements

The application's access is limited to specific content in Kaltura by using Entitlements. **Entitlements** is the relationship of a User with a given Category. By adding a User ID as a member of a specific Category, you enable that user to access the content in that category. Additionally, you can specify the level of access this user will have to the category - such as admin, contributor, moderator, or member. Read more about entitlements and user access rules [here](https://developer.kaltura.com/api-docs/Secure_Control_and_Govern/Content-Categories-Management.html). 

Limiting an application to specific content is done by creating a User ID for that application, assigning permissions to that user in the relevant categories, and then specifying that user ID when creating the AppToken.  

Users and entitlements can be added and configured via the KMC or directly through the API (which will be covered below). 

### Permissions for API Actions  

To achieve optimal security for how your apps access your account, you will want to carefully consider which API actions each app should be allowed to execute. When creating Kaltura Sessions, those API permissions are controlled via [Privileges](https://developer.kaltura.com/api-docs/VPaaS-API-Getting-Started/Kaltura_API_Authentication_and_Security.html). 

The **Privileges String** is made up of key-value pairs that determine the actions available to this Kaltura Session. 
When assigning appTokens to your apps, the easiest way to configure the permitted actions is with User Roles. Roles are created [in the KMC](https://kmc.kaltura.com/index.php/kmcng/administration/roles/list), and the ID is then mapped to the `setrole` privilege key in the permissions string. This allows you to easily manage the permitted actions for the app Token by editing the role at any time.


## Create the App Token 

Now let's walk through the steps to creating an App Token

### Step 1: User Content Access

We'll need to give access for specific content to a specific user. If you don't have a user yet, you can create one easily using the [`user.add`](https://developer.kaltura.com/console/service/user/action/add) action. 

```
user = KalturaUser()
user.id = "DummyUser@kaltura.com"

result = client.user.add(user);
print(result);
```
Additional user details can be found in the [console](https://developer.kaltura.com/console/service/user/action/add). That ID will be used for granting access, which you can do easily in the KMC. 

1. You'll need to first add entitlements to the category in question by going to Settings > Integration Settings and selecting Add Entitlement. You'll be given the option to select one of your existing categories and then enter a privacy context label, which will now be the unique name of these entitlements.  

2. If you head back to Content > Categories, when you edit the category, you'll now be able to see the Entitlements tab, where you'll be able to set restrictions on the category, like who can view its entries and their data, and who can publish to the category. For cases where only users with the relevant appToken should have access, select Private. 

3. At the bottom of the entitlements page, under Permitted Users, click Manage Users and add your user to the category, whether as a Member, Contributor, Moderator, or Manager.

You can also do this via the API with the [`categoryUser.add`](https://developer.kaltura.com/console/service/categoryUser/action/add) action. You'll need your category ID and user ID (string). 

```
categoryUser = KalturaCategoryUser()
categoryUser.categoryId = 123456789
categoryUser.permissionLevel = KalturaCategoryUserPermissionLevel.MEMBER
categoryUser.userId = "dummyuser@kaltura.com"

result = client.categoryUser.add(categoryUser);
print(result);
```

### Step 2: Create a user role 
Again, the simple way to create a user role is via the KMC, under administration (the icon of a person) and select Roles > Add Role. 
You'll have options to name and describe the new role (make it specific) and then select permitted actions. You'll see that for each action, there is the option to allow all options, or to select specific permissions under that category. For example, under Content Moderation, you may allow this User Role to perform all actions except for deleting. You can also switch off a specific action altogether. Hit save and you should now see your new user role in the list. 

Alternatively, if you know exactly which actions you'd like to include in your User Role (you can see all their names and descriptions in `permission.list`), you can use the `userRole.add` API action to create a new role. Be sure to set the status of your role to Active (1) 

*Note that you will not be able to see in the KMC any roles that are created outside the KMC.* 
You can see a list of all your existing roles, however, with the [`userRole.list`](https://developer.kaltura.com/console/service/userRole/action/list) action. Make note of the ID of your new user role as you'll be needing it for your appToken. 

### Step 3: Creating the App Token 

Let's bring it all together. We have a user, which has been given access to the relevant categories. We have a userrole, and its ID. We will use hash of type SHA256 and give the session a duration of one day. 

```
appToken = KalturaAppToken()
appToken.description = "AppToken for Demo"
appToken.hashType = KalturaAppTokenHashType.SHA256
appToken.sessionDuration = 86400
appToken.sessionPrivileges = "setrole:15737171"
appToken.sessionType = KalturaSessionType.USER
appToken.sessionUserId = "dummyuser@kaltura.com"

result = client.appToken.add(appToken);
print(result);
```

In the response you'll get an appToken object containing both an ID and a token. Hold on to those as you'll need them for the next steps. 

## Generate a Kaltura Session with the App Token 

The Kaltura Session generated with the appToken will have the content and action permissions that were configured in the apptoken. 

### Step 1: Create a Kaltura Session 

Because a Kaltura Session is required for every call to the API, we'll need to create an unprivileged session before being able to create the AppToken session. We use the [`session.startWidgetSession`] action with the widget ID, which is your partner ID with an underscore prefix. 

```
widgetId = "_1234569"
expiry = 86400

result = client.session.startWidgetSession(widgetId, expiry);
ks = result.ks 
```
The result will contain that unprivileged KS which you need for the next step 

### Step 2: Compute the Token Hash

We'll create a hash of the appToken `token` value together with the unprivileged KS, using a hash function in the language of your choice. 
*Make sure to use the same hash type as the one used for creating the appToken.*

```
hashString = hashlib.sha256(result.ks.encode('ascii') + appTokenValue.encode()).hexdigest()
```

The resulting string is the tokenHash which you'll use in the next step. 

### Step 3: Generate the Session 

We'll use the `appToken.startSession` action with the unprivileged KS, the hashToken, and the token `ID`. Note that you don't need to include a User ID or session privileges, as we've already associated those with the appToken, which will override anything added in this step. 

```
id = "<token ID>"
tokenHash = "<token hash>"
userId = ""
type = KalturaSessionType.USER
expiry = 3600
sessionPrivileges = ""

result = client.appToken.startSession(id, tokenHash, userId, type, expiry, sessionPrivileges);
print(result);

```

You'll notice in the response that the user ID is the same as the one you configured, as well as the role ID in the privileges string. The expiry is set to an hour (although you can change this), meaning that if you wish to change access permissions on this appToken, you can make those changes to the User or the Role associated with the appToken, and those changes will be reflected the next time a Kaltura Session is generated with this appToken. 


Congratulations! Your applications are now ready to use this KS to access the Kaltura API with your pre configured limitations. 



