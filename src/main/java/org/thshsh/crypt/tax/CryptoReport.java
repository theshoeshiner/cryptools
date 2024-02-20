package org.thshsh.crypt.tax;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.tax.ExchangeFile.Column;

@Component
@Scope("prototype")
public class CryptoReport {

	public static void main(String[] args) throws IOException {
		CryptoReport r = new CryptoReport();
		r.run();
	}

	@Autowired
	CryptoProcessor processor;
	
	TransactionManager manager;
	
	public void run() throws IOException {

		manager = new TransactionManager();
		manager.filePrefix = "2023";
		
		//configure transaction manager
		
		manager.putTypeString("buy", Transaction.Type.Buy);
		
		manager.putTypeString("sell", Transaction.Type.Sell);
		
		manager.putTypeString("withdrawal", Transaction.Type.Withdrawal);
		manager.putTypeString("send", Transaction.Type.Withdrawal);
		
		manager.putTypeString("deposit", Transaction.Type.Deposit);
		manager.putTypeString("receive", Transaction.Type.Deposit);
		
		manager.putTypeString("earn", Transaction.Type.Income);
		manager.putTypeString("income", Transaction.Type.Income);
		
		manager.putTypeString("order", null);
		
		ExchangeFile cbAdvBuyFile = new ExchangeFile("coinbaseadv-trades.csv");
		cbAdvBuyFile.putTypeString("advanced trade trade", Transaction.Type.Buy);
		cbAdvBuyFile.putTypeString("converted to", Transaction.Type.Buy);
		cbAdvBuyFile.putTypeString("converted from", Transaction.Type.Sell);
		
		cbAdvBuyFile.sumDuplicates = true;
		cbAdvBuyFile.exchange = "coinbase";
		cbAdvBuyFile.mapColumn(Column.Id,0);
		cbAdvBuyFile.mapColumn(Column.Type,1);
		cbAdvBuyFile.mapColumn(Column.Timestamp,2);
		
		cbAdvBuyFile.mapColumn(Column.Asset,3);
		cbAdvBuyFile.mapColumn(Column.Quantity,4);
		cbAdvBuyFile.mapColumn(Column.Price,5);
		
		cbAdvBuyFile.mapColumn(Column.Asset,7);
		cbAdvBuyFile.mapColumn(Column.Quantity,8);
		cbAdvBuyFile.mapColumn(Column.Price,9);
		
		
		//This only works if we know the fee seperately, which we dont have in this file
		//cbAdvFile.mapColumn(Column.PriceWithFee,5);
		
		//cbAdvBuyFile.forceColumn(Transaction.Field.Type, Transaction.Type.Buy);
		manager.addFile(cbAdvBuyFile);
		
		//same file but columns for sell transactions are different
		/*ExchangeFile cbAdvSellFile = new ExchangeFile("coinbaseadv-trades.csv");
		cbAdvSellFile.exchange = "coinbase";
		cbAdvSellFile.sumDuplicates = true;
		cbAdvSellFile.putTypeString("converted from", Transaction.Type.Sell);
		cbAdvSellFile.mapColumn(Column.Id,0);
		cbAdvSellFile.mapColumn(Column.Type,1);
		cbAdvSellFile.mapColumn(Column.Timestamp,2);
		cbAdvSellFile.mapColumn(Column.Asset,7);
		cbAdvSellFile.mapColumn(Column.Quantity,8);
		cbAdvSellFile.mapColumn(Column.Price,9);
		//cbAdvSellFile.forceColumn(Transaction.Field.Type, Transaction.Type.Sell);
		manager.addFile(cbAdvSellFile);*/
		
		ExchangeFile cbFile = new ExchangeFile("coinbase-transactions.csv");
		cbFile.exchange = "coinbase";
		cbFile.mapColumn(Column.Id,0);
		cbFile.mapColumn(Column.Timestamp,0);
		cbFile.mapColumn(Column.Type,1); //we use type to detect income and rewards
		cbFile.mapColumn(Column.Asset,2);
		cbFile.mapColumn(Column.Quantity,3);
		cbFile.mapColumn(Column.Fee,7);
		cbFile.mapColumn(Column.Price,5);
		cbFile.mapColumn(Column.Description,8);
		cbFile.priority=10;
		manager.addFile(cbFile);

		ExchangeFile cbpFile = new ExchangeFile("coinbasepro-trades.csv");
		cbpFile.exchange = "coinbasepro";
		cbpFile.mapColumn(Column.Id,1);
		cbpFile.mapColumn(Column.Timestamp,4);
		cbpFile.mapColumn(Column.Type,3);
		cbpFile.mapColumn(Column.Market,2);
		cbpFile.mapColumn(Column.Quantity,5);
		cbpFile.mapColumn(Column.PriceWithFee,9);
		cbpFile.mapColumn(Column.Fee,8);
		manager.addFile(cbpFile);
		
		

		ExchangeFile cbpTran = new ExchangeFile("coinbasepro-transactions.csv");
		cbpTran.exchange = "coinbasepro";
		cbpTran.mapColumn(Column.Id,6);
		cbpTran.mapColumn(Column.Timestamp,2);
		cbpTran.mapColumn(Column.Type,1);
		cbpTran.mapColumn(Column.Asset,5);
		cbpTran.mapColumn(Column.Quantity,3);
		cbpTran.priority=1;
		manager.addFile(cbpTran);

		{
			ExchangeFile ef = new ExchangeFile("kraken-trades.csv");
			ef.exchange = "kraken";
			ef.mapColumn(Column.Id,0);
			ef.mapColumn(Column.Market,2);
			ef.mapColumn(Column.Timestamp,3);
			ef.mapColumn(Column.Type,4);
			ef.mapColumn(Column.Quantity,9);
			ef.mapColumn(Column.Fee,8);
			ef.mapColumn(Column.Price,7);
			manager.addFile(ef);
		}

		{
			ExchangeFile ef = new ExchangeFile("kraken-transactions.csv");
			ef.exchange = "kraken";
			ef.mapColumn(Column.Id,0);
			ef.mapColumn(Column.Timestamp,2);
			ef.mapColumn(Column.Type,3);
			ef.mapColumn(Column.Asset,6);
			ef.mapColumn(Column.Quantity,7);
			ef.mapColumn(Column.Fee,8);
			manager.addFile(ef);
		}

		{
			ExchangeFile ef = new ExchangeFile("coinmetro.csv");
			ef.forceColumn(Transaction.Field.Type,Transaction.Type.Buy);
			ef.exchange = "coinmetro";
			ef.allowDuplicates = false;
			ef.swapFeeForBuy = true;
			ef.swapPriceForSell = true;

			ef.mapColumn(Column.Id,2);
			ef.mapColumn(Column.Timestamp,1);
			ef.mapColumn(Column.Type,2);
			ef.mapColumn(Column.Asset,0);
			ef.mapColumn(Column.Market,6);
			ef.mapColumn(Column.Fee,4);

			ef.mapColumn(Column.Quantity,3);
			ef.mapColumn(Column.Price,8);

			manager.addFile(ef);
		}

		{
			ExchangeFile btFile = new ExchangeFile("bittrex-trades.csv");
			btFile.exchange = "bittrex";
			btFile.mapColumn(Column.Id,0);
			btFile.mapColumn(Column.MarketInverse,1);
			btFile.mapColumn(Column.Timestamp,2);
			btFile.mapColumn(Column.Type,3);
			btFile.mapColumn(Column.Quantity,5);
			btFile.mapColumn(Column.Remaining,6);
			btFile.mapColumn(Column.Fee,7);
			btFile.mapColumn(Column.Price,8);
			manager.addFile(btFile);
		}

		{
			ExchangeFile btDep = new ExchangeFile("bittrex-deposits.csv");
			btDep.forceColumn(Transaction.Field.Type,Transaction.Type.Deposit);
			btDep.exchange = "bittrex";
			btDep.mapColumn(Column.Id,0);
			btDep.mapColumn(Column.Asset,1);
			btDep.mapColumn(Column.Quantity,2);
			btDep.mapColumn(Column.Timestamp,4);
			btDep.mapColumn(Column.Address,6);
			manager.addFile(btDep);
		}

		{
			ExchangeFile btWit = new ExchangeFile("bittrex-withdrawals.csv");
			btWit.forceColumn(Transaction.Field.Type,Transaction.Type.Withdrawal);
			btWit.exchange = "bittrex";
			btWit.mapColumn(Column.Id,0);
			btWit.mapColumn(Column.Asset,1);
			btWit.mapColumn(Column.Quantity,2);
			btWit.mapColumn(Column.Address,3);
			btWit.mapColumn(Column.Timestamp,4);
			btWit.mapColumn(Column.Fee,7);
			manager.addFile(btWit);
		}


		manager.addMissingTransfer("3cead614-af70-46d4-a5c7-ba2a5aaf2e94","coinbase");
		manager.addMissingTransfer("b3cb5979-3df4-41aa-8411-27e1ad647aa2","coinbase");
		manager.addMissingTransfer("ef3bd727-969b-444b-9c9c-2998ca85b2d3","coinbase");
		manager.addMissingTransfer("4cbeeeb7-03d3-4ee2-aec0-26512112ac99","coinbase");
		manager.addMissingTransfer("0599b07c-de3b-4629-a82b-cae3def1bfe5","coinbase");
		manager.addMissingTransfer("b6a20baa-c62a-4e17-a851-190b79e57efb","coinbase");
		manager.addMissingTransfer("3b15b5e2-1b2a-4fc5-a038-2bc66a32118d","coinbase");
		manager.addMissingTransfer("86853c5c-493a-49e8-bc1d-fc7b43792726","coinbase");
		manager.addMissingTransfer("bd2d2c45-a834-482a-a9bf-1ca194a5fcd8","coinbase");
		
		//fil
		manager.addMissingTransfer("9382c0e6-476a-47a5-b8a7-53e194706e01","coinbase");
		//dai
		manager.addMissingTransfer("d631c886-5edd-4707-819f-6a278bfe6677","coinbase");
		//grt
		manager.addMissingTransfer("0cb08c52-cb34-43bd-91b7-c72ced6ef40e","coinbase");
		//nu
		manager.addMissingTransfer("5946b9c0-b846-4b24-99d7-9e45a35d3f0f","coinbase");
		//band
		manager.addMissingTransfer("62d599ba-cad4-4e7a-aead-99cf6ac97c86","coinbase");
		//nmr
		manager.addMissingTransfer("8f9aeee0-4c1c-46df-b8ce-119406b7492d","coinbase");
		//forth
		manager.addMissingTransfer("0c8f0a76-92d4-4cb9-8857-37d0f9c35ce3","coinbase");
		//amp
		manager.addMissingTransfer("cf97e9dc-c668-455c-af00-068cfe9d53e4","coinbase");
		//cgld
		manager.addMissingTransfer("ec7ea90a-7af8-435d-a6ef-fd341c9f9813","coinbase");
		

		//salt was delisted while we still owned some
		//TODO find a better way to do this
		/*manager.addTransaction(
				new Transaction(
						"salt-delisted",
						LocalDateTime.of(2020, 2, 21, 00, 00).atZone(ZoneId.of("Z")),
						Transaction.Type.Sell,
						new BigDecimal("7.73027"),
						"SALT",
						"bittrex",
						new BigDecimal(0),
						new String[]{"SALT","USD"}
						));*/


		//This forces the processor to consider these transfers matched
		manager.forceMatch(Arrays.asList("2017-12-01T22:48:07Z","2017-12-01T23:16:21Z","43578355"));
		manager.forceMatch(Arrays.asList("2017-12-01T21:19:28Z","43570187"));
		manager.forceMatch(Arrays.asList("2017-12-26T13:50:10Z","2017-12-27T01:02:26Z","49316115"));

		//flag these transactions as external, which means they interacted with non-tracked acccounts (e.g. bank account)
		//tells the processor to not attempt to match them with anything
		manager.addExternalIds(Arrays.asList(
			//external buys from coinbase
			"2017-08-26T15:52:11Z",
			"2017-08-26T15:57:54Z",
			"2017-11-04T13:02:50Z",
			"2017-11-04T13:03:02Z",
			"2017-12-11T13:19:14Z",
			"2017-12-23T18:38:29Z",
			"2018-01-03T14:52:16Z",
			"2018-01-17T05:45:55Z",
			"2018-02-19T18:29:26Z",
			"2018-02-20T03:10:21Z",
			//coinbase pro USD deposits
			"41fdd3b3-c041-45a9-974e-52cbfd06ab24",
			"e6bb6aba-f85f-4d72-93e6-9e39a443285a",
			"f7fefc36-0793-4453-aaab-58206569ae98",
			"253f4504-93da-4d18-abe6-09abf8e72dc3",
			"24070b62-7f32-421b-a14e-8142ef0b0a9b",
			"590df565-ecc9-4ac7-a490-68d7796d69c0",
			"06e2e32a-0124-43ff-b1ba-c1b70392bf9d",
			"c207f45a-35e3-4d3e-b032-69bbaa40643f",
			"ff602830-d512-4778-8222-29b9c85b55af",
			//mifflin market withdrawals
			"2018-02-19T23:21:08Z",
			"2018-02-20T02:02:17Z",
			"2018-02-20T03:11:10Z"
			)
		);

		manager.loadAndInit();

		//processor = new CryptoProcessor("792c3be2e2607290ddb07ad8864a72ea33920bd12307b1dc08930f0431ed3921",exchange.transactionsList);

		processor.setYears(Arrays.asList(2017,2018,2019,2020,2021,2022, 2023,2024));
		
		processor.setTransactions(manager.transactionsList);
		processor.initTransactions();
		
		//FIXME REMOVE
		//throw new RuntimeException();
		
		processor.processTransactions();



		/*for(var record of processor.accounts.records){
			addToTable(record);
		}*/

		/*for(var record of processor.accounts.records){
			if(record instanceof SellRecordBlock) {
				addSaleRecord(record);
			}
		}*/

		processor.gainsReport();
		processor.balanceReport();

	}

	public CryptoProcessor getProcessor() {
		return processor;
	}

	public TransactionManager getManager() {
		return manager;
	}
	
	

}
