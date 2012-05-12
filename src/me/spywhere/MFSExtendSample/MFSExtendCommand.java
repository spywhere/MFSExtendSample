package me.spywhere.MFSExtendSample;

import lib.spywhere.MFS.DataType;
import lib.spywhere.MFS.Database;
import lib.spywhere.MFS.Field;
import lib.spywhere.MFS.Record;
import lib.spywhere.MFS.Result;
import lib.spywhere.MFS.Table;

import org.spout.api.ChatColor;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.exception.CommandException;

public class MFSExtendCommand {
	MFSExtendSample plugin;

	public MFSExtendCommand(MFSExtendSample instance){
		plugin=instance;
	}

	private boolean isNumber(String str)
	{
		return str.matches("-?\\d+(.\\d+)?");
	}

	@Command(aliases={"cart"}, usage="<cart>", desc="Simple cart system", min=1, max=5)
	public void cart(CommandContext args, CommandSource sender) throws CommandException {
		if(args.length()==1){
			if(args.getString(0).equalsIgnoreCase("demo")){
				Database db=plugin.mfs.getDB("Shop");
				if(db!=null){
					Table tbl=db.getTable("Cart");
					if(tbl!=null){
						if(tbl.addFieldAfter(new Field("TestField",DataType.String),new Field("Price"))){
							sender.sendMessage(ChatColor.BRIGHT_GREEN+"Done");
							return;
						}
					}
				}
				sender.sendMessage(ChatColor.BRIGHT_GREEN+"Failed");
				return;
			}
			if(args.getString(0).equalsIgnoreCase("demo2")){
				Database db=plugin.mfs.getDB("Shop");
				if(db!=null){
					Table tbl=db.getTable("Cart");
					if(tbl!=null){
						if(tbl.removeField(new Field("TestField"))){
							sender.sendMessage(ChatColor.BRIGHT_GREEN+"Done");
							return;
						}
					}
				}
				sender.sendMessage(ChatColor.BRIGHT_GREEN+"Failed");
				return;
			}
		}
		if(args.length()==2){
			if(args.getString(0).equalsIgnoreCase("view")){
				//Command:
				//   /cart view [CustomerName]
				//

				//Get database name "Shop"
				Database db=plugin.mfs.getDB("Shop");
				//If database exist
				if(db!=null){
					//Get table name "Cart"
					Table tbl=db.getTable("Cart");
					//If table exist
					if(tbl!=null){
						//Select all from table where Customer=args[1]
						Result result = tbl.filterRecord("Customer", args.getString(1));
						sender.sendMessage(ChatColor.CYAN+"Show all items in "+args.getString(1)+"'s cart: ");
						sender.sendMessage(ChatColor.CYAN+"ID : Item Name : Amount : Price/Each : Total Price");
						int sumprice=0;
						int sumitem=0;
						//If result is not empty
						if(result.totalRecord()>0){
							//Loop each record
							for(int i=0;i<result.totalRecord();i++){
								Record record = result.getRecord(i);
								int totalprice=(Integer.parseInt(record.getData(new Field("Price")))*Integer.parseInt(record.getData(new Field("Amount"))));
								sumprice+=totalprice;
								sumitem+=Integer.parseInt(record.getData(new Field("Amount")));
								sender.sendMessage(ChatColor.CYAN+record.getData(new Field("ID"))+" : "+record.getData(new Field("Item"))+" : "+record.getData(new Field("Amount"))+" : "+record.getData(new Field("Price"))+" : "+totalprice);
							}
							sender.sendMessage(ChatColor.CYAN+"Total Price: "+sumprice);
							sender.sendMessage(ChatColor.CYAN+"Total Item: "+sumitem);
							sender.sendMessage(ChatColor.CYAN+"================");
						}else{
							sender.sendMessage(ChatColor.CYAN+"No item in "+args.getString(1)+"'s cart.");
						}
						return;
					}else{
						sender.sendMessage(ChatColor.CYAN+"No item in "+args.getString(1)+"'s cart.");
						return;
					}
				}else{
					sender.sendMessage(ChatColor.CYAN+"No item in "+args.getString(1)+"'s cart.");
					return;
				}
			}
		}
		if(args.length()==5){
			if(args.getString(0).equalsIgnoreCase("add")){
				//Command:
				//   /cart add [CustomerName] [ItemName] [Price] [Amount]
				//
				if(isNumber(args.getString(3))&&isNumber(args.getString(4))){
					//Create new database called "Shop"
					Database db=plugin.mfs.createNewDB("Shop");
					//If a new database is exist
					if(db==null){
						//Load it
						db=plugin.mfs.getDB("Shop");
					}
					//Create new table called "Cart" and have "Customer, Item, Price, Amount" as a field
					Table tbl = db.createNewTable("Cart",new Field("ID",DataType.Integer),new Field("Customer",DataType.String),new Field("Item",DataType.String),new Field("Price",DataType.Integer),new Field("Amount",DataType.Integer));
					//If a new table is exist
					if(tbl==null){
						//Load it
						tbl=db.getTable("Cart");
					}
					//Add a new record (item) into Cart
					int amount = Integer.parseInt(args.getString(4));
					//Select all from table where Customer=args[1] and Item=args[2] and Price=args[3]
					Result existitem = tbl.filterRecord("Customer", args.getString(1)).filterBy("Item", args.getString(2)).filterBy("Price", args.getString(3));
					//If result is not empty
					if(existitem.totalRecord()>0){
						amount=Integer.parseInt(existitem.getRecord(0).getData(3));
						amount+=Integer.parseInt(args.getString(4));
						//Update Amount=amount to  all record which select from table where Customer=args[1] and Item=args[2] and Price=args[3]
						tbl.updateRecords(tbl.filterRecord("Customer", args.getString(1)).filterBy("Item", args.getString(2)).filterBy("Price", args.getString(3)), "Amount", Integer.toString(amount));
						sender.sendMessage(ChatColor.CYAN+"Item in cart updated.");
					}else{
						//Add new record
						//   Customer | Item | Price | Amount
						//   args[1] | args[2] | args[3] | args[4]
						//
						tbl.addRecord(tbl.autoIncrement(new Field("ID")),args.getString(1),args.getString(2),args.getString(3),Integer.toString(amount));
						sender.sendMessage(ChatColor.CYAN+"Item added to cart.");
					}
					return;
				}else{
					sender.sendMessage(ChatColor.YELLOW+"/cart add [CustomerName] [ItemName] [Price] [Amount]");
					sender.sendMessage(ChatColor.YELLOW+"/cart add [Text/String] [Text/String] [Number] [Number]");
					return;
				}
			}
		}
	}
}
