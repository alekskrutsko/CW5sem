package serverFiles;

import personalInfo.User;

public class ServerMSG {
    private User user;
    private CommandTypes commandType;
    private String data;

    public enum CommandTypes{
        SIGNIN,
        SIGNUP,
        NEWAPPRAISER,
        BLOCKUSER,
        DELETEAPPRAISER,
        GETAPPRAISERS,
        EDITAPPRAISER,
        GETCONSUMERS,
        FILTERUSERS,
        GETUSERS,
        EDITUSER,
        ADDOBJECT,
        GETOBJECTS,
        DELETEOBJECT,
        FILTEROBJECTS,
        EDITOBJECT,
        NEWOBJECTTYPE,
        DELETEOBJECTTYPE,
        GETOBJECTSTYPES,
        GETUSERCONTRACTS,
        NEWCONTRACT,
        CONTRACTWAITFORCONSUMER,
        CONTRACTWAITFORAPPRAISER,
        CONTRACTTERMINATED,
        CONTRACTSIGNED,
        GETALLCONTRACTS,
        CONSUMERDIAGRAM,
        FILTERCONTRACTS,
        ADMINPIECHART,   //КРУГОВАЯ ДИАГРАММА
        LINECHART,       //ЛИНЕЙНАЯ ДИАГРАММА
        BESTAPPRAISER,
        USERPIECHART;
    }

    public ServerMSG(){
        commandType = null;
        data = null;
        user = new User();
    };

    public ServerMSG(User user, CommandTypes commandType, String data){
        this.user = user;
        this.commandType = commandType;
        this.data = data;
    }

    public CommandTypes getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandTypes commandType) {
        this.commandType = commandType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}