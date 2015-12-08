##GENERIC MESSENGER V1.0

- Justin Head
- Nam Dinh
- Cristian Ventura
- Satsuki Ueno
- Brendan Honea

####Overview
The purpose of this app is provide the user with a very easy to use SMS messaging interface that is compatible with the stock Android contacts app.

####Create Message
When you press the create message button, you can choose the recipient either by manually entering the phone number or choosing a contact from the default contacts app. You can also add a number as a new contact.

####Search Message
The search message button will allow you to search the message database for a message that contains whatever string the user passes as input. Clicking on one of the results will take you to the thread containing the selected message.

####Conversations
Clicking on an existing thread will you view all of the messages that have been exchanged between you and the person you want to talk to. From this screen you can also delete messages or forward messages by holding down on the message you want to delete/forward.


Javadoc can be found on the gh-pages branches, under the docs folder.
http://jhead.github.io/CS3354-SMS-Messenger/docs/

GitHub repository can be found here:
https://github.com/jhead/CS3354-SMS-Messenger

Tests are under app/src/main/java/cs3354group10/messenger/test

Contribution of each member:
Satsuki Ueno: sending and receiving message, UI improvements, ThreadViewBinder.
Justin Head: SQLite message database and thread list activity
Nam Dinh: View messages of a contact in ThreadView, deleting message and thread.
Cristian Ventura: Contact related activitys and some of the appearance of the app.
Brendan Honea: Search functionality and ThreadView activity.