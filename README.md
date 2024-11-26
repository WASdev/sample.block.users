# Servlet Filter to Block an authorized user from using a web app

### Description 

This project contains a sample Servlet Filter that can be downloaded, built and
deployed into the WebSphere Application Server admin console to block users from 
accessing the admin console while allowing them access to make changes via wsadmin.

This might be of use where there is a user that is configured for wsadmin access
to process change requests, but where there is a desire to ensure no one can use that
account to make changes in the admin console. It assumes that the change system is
able to audit wsadmin access, but is unable to correctly audit if someone were to
use the wsadmin account from the admin console.

### How to build the project

To build you will need Git, Java SE 8 or newer.

#### Get the source code

```
git clone git@github.com:WASdev/blockUsers.git
cd blockUsers
```

#### Build from the Command Line

To compile the code run:

```
mvnw package
```

The first time it runs it will download maven and any dependencies compile and build it.
The build jar will be available in `target/blockUsers.jar`

### How to deploy the code

To deploy the code copy the `target/blockUsers.jar` to `${WAS_HOME}/systemApps/isclite.ear/isclite.war/WEB-INF/lib/`.
This only needs to be copied onto a server running the admin console. So in a WebSphere Application Server Network
Deployment system you only need to do this on the node running the deployment manager.

### How to Block a user

To block a user create a file called `console.user.block.list` in the `properties` directory in the profile home.
For each user that you wish to block add that user name on a line by itself terminated with a new line character.

### How to hardcode a blocked user

To hardcode a blocked user account in the maven project create a file `src/main/resources/console.user.block.list`.
Add any users that you want to hardcode into this file. Build and deploy this package.

### How to customise the blocked user web page

To customize the page that is showed when a blocked user logs go to the maven project and replace the content of 
`src/main/resources/blockedUsersMessage.html` file with whatever html code that you wish to appear. If there is a desire
for images, javascript or stylesheets they must be either inline into this html file, or they must be referenced
via an external web page.