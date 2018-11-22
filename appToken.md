# Application Session Management 

An Application Token is useful in cases where different applications with varying permissions need access to your Kaltura account, without using your Admin Secret. 
The appToken is created and customized by the account administrator, and then used by the developers to generate Kaltura Sessions for their respective applications. This allows access to the API to be revoked at any time with the deletion of the appToken. 

## Before You Start

Before you create an appToken, you need to decide whether to create a "blank" appToken, or one preconfigured with permissions. If your only concern is giving access without sharing your admin secret, a basic appToken is sufficient. But if you want to always limit the permissions of a specific application, you'll need to create the appToken with preconfigured [Privileges](https://developer.kaltura.com/api-docs/VPaaS-API-Getting-Started/Kaltura_API_Authentication_and_Security.html). Any configurations included in the creation of the appToken *cannot* be overriden when the session is created with that appToken. 

The **Privileges String** that could be included in the appToken is made up of key-value pairs that determine the actions available to this Kaltura Session. Some keys that may be useful for your appToken are: 
- `setrole`: When assigning appTokens to your apps, the easiest way to configure the permitted actions is with User Roles. Roles are created [in the KMC](https://kmc.kaltura.com/index.php/kmcng/administration/roles/list), and give you the option of adding and removing specific actions available to the app. The ID of the Role is then mapped to the `setrole` privilege key in the permissions string. This allows you to easily manage the permitted actions by editing the role at any time after.
- `privacycontext`: If you want to limit the app to the content of a specific category, you could [set entitlements](https://kmc.kaltura.com/index.php/kmcng/settings/integrationSettings) on that category and map it to the `privacycontext` key (examples below). Keep in mind however, that all end users who will need to access that content must be members of the category.


## Create the App Token 

We will cover appToken creation with and without pre configured privileges. Notice that the appToken has a sessionType. If set to type ADMIN (2), any session created with it will be a basic ADMIN session. If set to USER (0), however, various admin operations, such as `List`, will not be available.  A USER appToken would be useful in cases where the application is only uploading media but not viewing it afterwards. Furthermore, we recommend using hash of type `SHA256`, but whichever you use, make sure be consistent in the session creation. 

### Basic App Token 

We'll start with a basic, "blank" appToken. This is an appToken without privileges, without a user, and without an expiry date. While a UserID *can* be added in the [`appToken.startSession`](https://developer.kaltura.com/console/service/appToken/action/startSession) action, no additional privileges can be added after the appToken is created. 

```
appToken = KalturaAppToken()
appToken.description = "Basic App Token"
appToken.hashType = KalturaAppTokenHashType.SHA256
appToken.sessionType = KalturaSessionType.ADMIN

result = client.appToken.add(appToken);
print(result);
```
In the result you'll see an `ID` as well as a `token`. Hold on to those as you'll need them for session creation. You can also view all your appTokens with the [`appToken.list`](https://developer.kaltura.com/console/service/appToken/action/list) action. 

### Set a User Role

The easy way to create a User Role is [in the KMC](https://kmc.kaltura.com/index.php/kmcng/administration/roles/list). You'll have options to name and describe the new role (make it specific) and then select permitted actions. You'll see that for each action, there is the option to allow all options, or to select specific permissions under that category. For example, under Content Moderation, you may allow this User Role to perform all actions except for deleting. You can also switch off a specific action altogether. Hit save and you should now see your new user role in the list. 

Alternatively, if you know exactly which actions you'd like to include in your User Role (you can see all their names and descriptions in `permission.list`), you can use the `userRole.add` API action to create a new role. Be sure to set the status of your role to Active (1) 

*Note that you will not be able to see in the KMC any roles that are created outside the KMC.* 
You can see a list of all your existing roles, however, with the [`userRole.list`](https://developer.kaltura.com/console/service/userRole/action/list) action. Make note of the ID of your new user role as you'll be needing it for your appToken, where you can set the role like this: 

```
appToken.sessionPrivileges = "setrole:1234567"
```

### Add a Privacy Context 

Adding a privacy context will limit the session to the contents of one category. To enable entitlements on the category, select Add Entitlements in the Integration Settings in [KMC](https://kmc.kaltura.com/index.php/kmcng/settings/integrationSettings). Select a category and give it a Privacy Context Label. That is the name that should be used in the Privileges String when adding the `privacycontext` key. 

```appToken.sessionPrivileges = "setrole:1234567,privacycontext:application"```

### Add a User to the Category

Remember that for users to access this category, they also need to be members of the category, which can be done in the Entitlements tab in the Category Settings, or with the [`categoryUser.add`](https://developer.kaltura.com/console/service/categoryUser/action/add) action, where you'll need the category ID and the user ID, which can be any string identifying that user. 

```
categoryUser = KalturaCategoryUser()
categoryUser.categoryId = 123456789
categoryUser.permissionLevel = KalturaCategoryUserPermissionLevel.MEMBER
categoryUser.userId = "DummyUser"

result = client.categoryUser.add(categoryUser);
print(result);
```

### Add a User to the AppToken 

Another way to manage content access for the appToken session, is by including a user in the appToken creation. This user, a dummy user of sorts, is added to the relevant categories, allowing the application access to all those categories. Because a different user cannot be set in the session creation, all sessions using that appToken will belong to the same user, which could be a problem where user-specific anlaytics are involved. 

Let's bring it all together. We have a user, which has been given access to the relevant categories. We have a userrole, and its ID. We will use hash of type SHA256 and give the session a duration of one day. 

```
appToken = KalturaAppToken()
appToken.description = "App Token with User and Privileges"
appToken.hashType = KalturaAppTokenHashType.SHA256
appToken.sessionDuration = 86400
appToken.sessionPrivileges = "setrole:1234567"
appToken.sessionType = KalturaSessionType.ADMIN
appToken.sessionUserId = "dummyuser@kaltura.com"

result = client.appToken.add(appToken);
print(result);
```

Reminder that you can view all your appTokens with the [`appToken.list`](https://developer.kaltura.com/console/service/appToken/action/list) action. 

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

We'll use the [`appToken.startSession`](https://developer.kaltura.com/console/service/appToken/action/startSession) action with the unprivileged KS, the hashToken, and the token `ID`. If you created an appToken with a user, it will override a user added here: 

```
id = "<token ID>"
tokenHash = "<token hash>"
userId = "enduser"
type = KalturaSessionType.ADMIN
expiry = 3600
sessionPrivileges = ""

result = client.appToken.startSession(id, tokenHash, userId, type, expiry, sessionPrivileges);
print(result);

```

You'll notice that the response contains any configurations from the appToken creation, regardless of what was passed in during the startSession. The expiry is set to an hour (although you can change this), meaning that after that time has passed, a new session will need to be generated. So if you wish to change access permissions on this appToken, you can make those changes to the Role, User, or Privacy Context associated with the appToken, and those changes will be reflected the next time a Kaltura Session is generated with this appToken. 


Congratulations! Your applications are now ready to use this KS to access the Kaltura API with your pre configured limitations. 



