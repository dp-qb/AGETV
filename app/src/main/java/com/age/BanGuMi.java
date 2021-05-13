public class BanGuMi
{
	public Boolean isnew;
	public String id;
	public String wd;
	public String name;
	public String mtime;
	public String new_name;
    /*
     "isnew": false,
     "id": "20200087",
     "wd": 1,
     "name": "ReBIRTH",
     "mtime": "2020-09-22 12:46:08",
     "namefornew": "第24话"
     */
    public BanGuMi(String is,String i,String w,String n,String m,String na)
    {
        isnew=Boolean.parseBoolean(is);
        id=i;
        wd=w;
        name=n;
        mtime=m;
        new_name=na;
    }
	/*
    public BanGuMi(String i,String n)
    {
        isnew=false;
        id=i;
        wd="周几";
        name=n;
        mtime="最后更新时间";
        new_name="最新一集标题";
    }*/
	public BanGuMi()
	{
		isnew=false;
        id=null;
        wd=null;
        name=null;
        mtime=null;
        new_name=null;
	}
	public void setisnew(Boolean isnew) {
		if(isnew==true)this.isnew = true;
		else this.isnew = false;
		}  
	public void setid(String id) {this.id = id;}  
	public void setwd(String wd) {this.wd = wd;}  
	public void setname(String name) {this.name = name;}  
	public void setmtime(String mtime) {this.mtime = mtime;}  
	public void setnew_name(String new_name) {this.new_name = new_name;}  
	
	
    public boolean getisnew(){return isnew;}
    public String getid(){return id;}
    public String getid_8(){
        String d;
        if(id.length()<=8){d=id;}
        else{d=id.substring(0,8);}
        return d;}
    public String getwd(){return wd;}
    public String getname(){return name;}
    public String getmtime(){return mtime;}
    public String getnew_name(){return new_name;}
	/*
	public BanGuMi getBanGuMi(String is,String i,String w,String n,String m,String na)
    {
        this.isnew=Boolean.parseBoolean(is);
        this.id=i;
        this.wd=w;
        this.name=n;
        this.mtime=m;
		this.new_name=na;
		return this;
    }*/
    
}
