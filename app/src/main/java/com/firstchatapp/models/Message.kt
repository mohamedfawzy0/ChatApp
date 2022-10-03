package com.firstchatapp.models

 class Message{
     var messageId: String?=""
     var message: String?=""
     var myId: String?=""
     var imageUrl:String?=""
     var timeStamp:Long?=0
     constructor()
     constructor(message: String?="",myId: String?="",timeStamp: Long?=0){
         this.message=message
         this.myId=myId
         this.timeStamp=timeStamp
     }
 }