function doPost(data){
  try{
    Logger.log(data);
    
    var app = SpreadsheetApp;
    var activeSheet=app.getActiveSpreadsheet().getActiveSheet();
    
    var arr=JSON.parse(JSON.stringify(data.parameters));
    var lr = activeSheet.getLastRow();
    
    var d=new Date();
    var time=d.getYear();
    time=time*100+d.getMonth()+1;
    time=time*100+d.getDate();
    time=time*100+d.getHours();
    time=time*100+d.getMinutes();
    time=time*100+d.getSeconds();
    
    activeSheet.getRange(lr+1,1).setValue(time);
    activeSheet.getRange(lr+1,2).setValue(arr['Name']);
    activeSheet.getRange(lr+1,3).setValue(arr['Email']);
    activeSheet.getRange(lr+1,4).setValue(arr['Message']);
    
    var mailMessage = 
        time+"\n"
        +arr['Name']+"\n"
        +arr['Email']+"\n"
        +arr['Message'];
    
    MailApp.sendEmail("HOST@EMAIL.com", "Comments from foevs.in", mailMessage);
    
    mailMessage = 
      mailMessage
      +"\n\nThank you for your message. You'll get a reply soon";
    MailApp.sendEmail(arr['Email'], "Comments sent to foevs.in", mailMessage);
    
    var html="<script>alert('Message sent successfully!');window.history.back();</script>";
    return HtmlService.createHtmlOutput(html);
  }
  catch(error){
    Logger.log(error);
    return ContentService.createTextOutput(JSON.stringify({"result":"error",
                      "data":error}))
    .setMimeType(ContentService.MimeType.JSON);
  }
}
