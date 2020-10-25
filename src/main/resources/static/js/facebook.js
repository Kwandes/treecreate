
    window.fbAsyncInit = function()
    {
        FB.init({
            appId      : '353337832643556',
            cookie     : true,                     // Enable cookies to allow the server to access the session.
            xfbml      : true,                     // Parse social plugins on this webpage.
            version    : 'v8.0'           // Use this Graph API version for this call.
        });

        FB.AppEvents.logPageView();
    };

    (function(d, s, id)
    {
         var js, fjs = d.getElementsByTagName(s)[0];
         if (d.getElementById(id)) {return;}
         js = d.createElement(s); js.id = id;
         js.src = "https://connect.facebook.net/en_US/sdk.js";
         fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));

    // Called when a person is finished with the Login Button.
    function checkLoginFacebookStatus()
    {
        FB.getLoginStatus(function(response)
        {
            statusChangeCallback(response);
        });
    }

    //Log in with facebook
    function loginWithFacebook()
    {
        FB.login(function(response) {
            if (response.authResponse) {
             console.log('Welcome!  Fetching your information.... ');
             FB.api('/me', function(response) {
               testAPI();
             });
            } else {
             console.log('User cancelled login or did not fully authorize.');
            }
        }, {scope: 'email, user_friends, user_birthday', //Retrieve info from facebook login
            return_scopes: true //This is so you recieve a list of the granted permissions in the grantedScopes field on the authResponse obj.
        });
        /* GET:
        Personal email
        Users friend list to create treecreate designs from friend list
        Users birthday for birthday promotions
        */

    }

    function testAPI() { //testing Facebook API

        console.log('Welcome!  Fetching your information.... ');
        FB.api('/me', function(response) {
            console.log('Good to see you, ' + response.name + '.');
        });
        //get users Email
        FB.api('/me', {fields: 'email, first_name, last_name'}, function(response) {
          console.log(response);
        });
    }



