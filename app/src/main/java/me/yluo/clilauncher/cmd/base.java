package me.yluo.clilauncher.cmd;


import me.yluo.clilauncher.data.ExecContext;

public abstract class base {

    protected ExecContext EXECCONTEXT;
    public final String exec(ExecContext execContext) throws Exception{
        this.EXECCONTEXT = execContext;
        int arglen = (execContext.args==null)?0:execContext.args.length;
        if(arglen>getArgsNum()[1]){
            return "Args number error !!! \ncommandStr \""+execContext.commandStr +"\" max need "+getArgsNum()[1]+" args!!!";
        }else if(arglen<getArgsNum()[0]){
            return "Args number error !!! \ncommandStr \""+execContext.commandStr +"\" at least need "+getArgsNum()[0]+" args!!!";
        }
        return execCommand();
    }


    protected abstract String execCommand() throws Exception;

    public abstract int argType(int i);

    public abstract int[] getArgsNum();

    public abstract String getUsageInfo();

    public abstract boolean isAsync();
}
