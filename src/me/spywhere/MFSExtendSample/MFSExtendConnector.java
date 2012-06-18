package me.spywhere.MFSExtendSample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import org.spout.api.exception.InvalidDescriptionFileException;
import org.spout.api.exception.InvalidPluginException;
import org.spout.api.exception.UnknownDependencyException;
import org.spout.api.plugin.PluginDescriptionFile;
import org.spout.api.plugin.PluginManager;

import lib.spywhere.MFS.MFSExtend;
import lib.spywhere.MFS.StorageType;

public class MFSExtendConnector{
	private static Logger log=Logger.getLogger("Minecraft");
	private static MFSExtend mfs=null;
	private static PluginManager pm=null;
	
	public static boolean isConnected(){
		return (mfs!=null);
	}

	static public MFSExtend getMFS(PluginManager ipm,String url, String user, String password, StorageType type){
		loadPlugin(ipm);
		pm=ipm;
		if(pm.getPlugin("MFSExtend")!=null){
			if(!new File("lib",type.getFileName()).exists()){
				log.info("[MFSExtendConnector] Downloading "+type.getName()+" library from server...");
				if(!downloadLib(type)){
					log.severe("[MFSExtendConnector] Error downloading "+type.getName()+" library.");
					return null;
				}
				log.info("[MFSExtendConnector] Download successful.");
			}
			mfs=(MFSExtend)pm.getPlugin("MFSExtend");
			mfs.setMFS(url,user,password,type);
			return mfs;
		}
		return null;
	}

	static public MFSExtend getMFS(PluginManager ipm, StorageType type){
		return getMFS(ipm,"","","",type);
	}

	static private boolean downloadLib(StorageType type){
		try {
			if(!new File("lib",type.getFileName()).exists()){
				if(!type.equals(StorageType.FLATFILE)&&!type.equals(StorageType.YML)){
					download("lib",type.getLibraryURL(), type.getFileName());
				}
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	static private boolean download(int time){
		try {
			if(time>=2){return true;}
			if(time==0){
				download("plugins",Mirror.Server1.getUrl(), "MFS.jar");
			}
			if(time==1){
				download("plugins",Mirror.Server2.getUrl(), "MFS.jar");	
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	static private void loadPlugin(PluginManager pm){
		if(pm.getPlugin("MFSExtend")==null){
			try {
					pm.loadPlugin(new File("plugins","MFS.jar"));
			} catch (UnknownDependencyException e) {
				log.severe("[MFSExtendConnector] Error UnknownDependency: "+e.getMessage());
			} catch (InvalidPluginException e) {
				log.severe("[MFSExtendConnector] Error InvalidPlugin: "+e.getMessage());
			} catch (InvalidDescriptionFileException e) {
				log.severe("[MFSExtendConnector] Error InvalidDescriptionFile: "+e.getMessage());
			}
		}else{
			if(!pm.getPlugin("MFSExtend").isEnabled()){
				pm.enablePlugin(pm.getPlugin("MFSExtend"));
			}
		}
	}

	static public boolean prepareMFS(PluginDescriptionFile pdf,PluginManager pm){
		if(!new File("plugins","MFS.jar").exists()){
			log.info("[MFSExtendConnector] Downloading MFS.jar from server...");
			int m=0;
			while(!download(m)){
				m++;
				if(m<2){
					log.info("[MFSExtendConnector] Downloading MFS.jar from Server "+(m+1)+"...");
				}
			}
			if(m>=2){
				log.severe("[MFSExtendConnector] Error downloading MFS.jar.");
				return false;
			}else{
				log.info("[MFSExtendConnector] Download successful.");
			}
		}
		log.info("[MFSExtendConnector] Starting MFSExtend...");	
		loadPlugin(pm);
		return true;
	}

	private static enum Mirror{
		
		Server1("http://dl.dropbox.com/u/65468988/Plugins/MFS/Stable%20Build/v0.3.1/MFS.jar"),
		Server2("http://dev.bukkit.org/media/files/598/656/MFS.jar");

		private Mirror(String url){
			this.url=url;
		}

		String url;
		public String getUrl() {
			return this.url;
		}
	}

	protected static boolean cancelled;
	public synchronized void cancel()
	{
		cancelled = true;
	}

	protected static synchronized void download(String fdr,String location, String filename) throws IOException {
		URLConnection connection = new URL(location).openConnection();
		connection.setUseCaches(false);
		String destination = fdr + File.separator + filename;
		File parentDirectory = new File(destination).getParentFile();
		if (parentDirectory != null) {
			parentDirectory.mkdirs();
		}
		InputStream in = connection.getInputStream();
		OutputStream out = new FileOutputStream(destination);
		byte[] buffer = new byte[65536];

		while (!cancelled)
		{
			int count = in.read(buffer);
			if (count < 0) {
				break;
			}
			out.write(buffer, 0, count);
		}

		in.close();
		out.close();
	}
}
