package me.spywhere.MFSExtendSample;

import java.util.logging.Logger;

import lib.spywhere.MFS.MFSExtend;
import lib.spywhere.MFS.StorageType;

import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.PluginDescriptionFile;

public class MFSExtendSample extends CommonPlugin{ 
	private Logger log=Logger.getLogger("Minecraft");
	private PluginDescriptionFile pdf=null;
	MFSExtend mfs=null;
	
	public void onEnable() {
		pdf=this.getDescription();

		if(MFSExtendConnector.prepareMFS(pdf,this.getGame().getPluginManager())){
			//MFSExtend prepared and can be connected now
			//Connect as MySQL
			//   mfs = MFSExtendConnector.getMFS(this.getGame().getPluginManager(), "localhost:8889","root","root",StorageType.MYSQL);
			//Connect as PostgreSQL
			//   mfs = MFSExtendConnector.getMFS(this.getGame().getPluginManager(), "localhost:8889","root","root",StorageType.POSTGRE);
			//Connect as H2
			//   mfs = MFSExtendConnector.getMFS(this.getGame().getPluginManager(), "localhost","sa","",StorageType.H2);
			//Connect as SQLite
			   mfs = MFSExtendConnector.getMFS(this.getGame().getPluginManager(), StorageType.SQLITE);
			//Connect as YML
			//   mfs = MFSExtendConnector.getMFS(this.getGame().getPluginManager(), StorageType.YML);
			//Connect as FlatFile
			//   mfs = MFSExtendConnector.getMFS(this.getGame().getPluginManager(), StorageType.FLATFILE);
			log.info("["+pdf.getName()+"] MFSExtend found and connected.");
		}else{
			//MFSExtend failed to download/install/run
			log.severe("["+pdf.getName()+"] Failed to run MFSExtend. Plugin now disabled.");
			getGame().getPluginManager().disablePlugin(getGame().getPluginManager().getPlugin(pdf.getName()));
			return;
		}
		this.getGame().getRootCommand().addSubCommands(this, MFSExtendCommand.class,new AnnotatedCommandRegistrationFactory(new SimpleInjector(new Object[] { this }), new SimpleAnnotatedCommandExecutorFactory()));
		log.info("["+pdf.getName()+"] v"+pdf.getVersion()+" successfully enabled.");
	}

	public void onDisable() {
		log.info("["+pdf.getName()+"] v"+pdf.getVersion()+" successfully disabled.");
	}
}