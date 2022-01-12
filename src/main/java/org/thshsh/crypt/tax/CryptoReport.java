package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.thshsh.crypt.tax.ExchangeFile.Column;

public class CryptoReport {

	public static void main(String[] args) {
		CryptoReport r = new CryptoReport();
		r.run();
	}

	public void run() {

		TransactionManager exchange = new TransactionManager();

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
		exchange.addFile(cbFile);

		ExchangeFile cbpFile = new ExchangeFile("coinbasepro-trades.csv");
		cbpFile.exchange = "coinbasepro";
		cbpFile.mapColumn(Column.Id,1);
		cbpFile.mapColumn(Column.Timestamp,4);
		cbpFile.mapColumn(Column.Type,3);
		cbpFile.mapColumn(Column.Market,2);
		cbpFile.mapColumn(Column.Quantity,5);
		cbpFile.mapColumn(Column.PriceWithFee,9);
		cbpFile.mapColumn(Column.Fee,8);
		exchange.addFile(cbpFile);

		ExchangeFile cbpTran = new ExchangeFile("coinbasepro-transactions.csv");
		cbpTran.exchange = "coinbasepro";
		cbpTran.mapColumn(Column.Id,6);
		cbpTran.mapColumn(Column.Timestamp,2);
		cbpTran.mapColumn(Column.Type,1);
		cbpTran.mapColumn(Column.Asset,5);
		cbpTran.mapColumn(Column.Quantity,3);
		cbpTran.priority=1;
		exchange.addFile(cbpTran);

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
			exchange.addFile(ef);
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
			exchange.addFile(ef);
		}

		{
			ExchangeFile ef = new ExchangeFile("coinmetro.csv");
			ef.force.put("type",Transaction.Type.Buy);
			ef.negativeQuantity = true;
			ef.exchange = "coinmetro";
			ef.allowDuplicates = false;

			ef.mapColumn(Column.Id,2);
			ef.mapColumn(Column.Timestamp,1);
			ef.mapColumn(Column.Type,2);
			ef.mapColumn(Column.Asset,0);
			ef.mapColumn(Column.MarketInverse,6);
			ef.mapColumn(Column.Fee,4);

			ef.mapColumn(Column.Quantity,3);
			ef.mapColumn(Column.Price,8);

			exchange.addFile(ef);
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
			exchange.addFile(btFile);
		}

		{
			ExchangeFile btDep = new ExchangeFile("bittrex-deposits.csv");
			btDep.force.put("type",Transaction.Type.Deposit);
			btDep.exchange = "bittrex";
			btDep.mapColumn(Column.Id,0);
			btDep.mapColumn(Column.Asset,1);
			btDep.mapColumn(Column.Quantity,2);
			btDep.mapColumn(Column.Timestamp,4);
			btDep.mapColumn(Column.Address,6);
			exchange.addFile(btDep);
		}

		{
			ExchangeFile btWit = new ExchangeFile("bittrex-withdrawals.csv");
			btWit.force.put("type",Transaction.Type.Withdrawal);
			btWit.exchange = "bittrex";
			btWit.mapColumn(Column.Id,0);
			btWit.mapColumn(Column.Asset,1);
			btWit.mapColumn(Column.Quantity,2);
			btWit.mapColumn(Column.Address,3);
			btWit.mapColumn(Column.Timestamp,4);
			btWit.mapColumn(Column.Fee,7);
			exchange.addFile(btWit);
		}


		exchange.addMissingTransfer("3cead614-af70-46d4-a5c7-ba2a5aaf2e94","coinbase");
		exchange.addMissingTransfer("b3cb5979-3df4-41aa-8411-27e1ad647aa2","coinbase");
		exchange.addMissingTransfer("ef3bd727-969b-444b-9c9c-2998ca85b2d3","coinbase");
		exchange.addMissingTransfer("4cbeeeb7-03d3-4ee2-aec0-26512112ac99","coinbase");
		exchange.addMissingTransfer("0599b07c-de3b-4629-a82b-cae3def1bfe5","coinbase");
		exchange.addMissingTransfer("b6a20baa-c62a-4e17-a851-190b79e57efb","coinbase");
		exchange.addMissingTransfer("3b15b5e2-1b2a-4fc5-a038-2bc66a32118d","coinbase");
		exchange.addMissingTransfer("86853c5c-493a-49e8-bc1d-fc7b43792726","coinbase");
		exchange.addMissingTransfer("bd2d2c45-a834-482a-a9bf-1ca194a5fcd8","coinbase");

		//salt was delisted while we still owned some
		//TODO find a better way to do this
		exchange.addTransaction(
				new Transaction(
						"salt-delisted",
						LocalDateTime.of(2020, 2, 21, 00, 00),
						Transaction.Type.Sell,
						new BigDecimal("7.73027"),
						"SALT",
						"bittrex",
						new BigDecimal(0),
						new String[]{"SALT","USD"}
						));


		//This forces the processor to consider these transfers matched
		exchange.forceMatch(Arrays.asList("2017-12-01T22:48:07Z","2017-12-01T23:16:21Z","43578355"));
		exchange.forceMatch(Arrays.asList("2017-12-01T21:19:28Z","43570187"));
		exchange.forceMatch(Arrays.asList("2017-12-26T13:50:10Z","2017-12-27T01:02:26Z","49316115"));

		//flag these transactions as external, which means they interacted with non-tracked acccounts (e.g. bank account)
		exchange.addExternalIds(Arrays.asList(
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
			//mifflin market withdrawals
			"2018-02-19T23:21:08Z",
			"2018-02-20T02:02:17Z",
			"2018-02-20T03:11:10Z"
			)
		);

		exchange.loadAndInit();

		CryptoProcessor processor = new CryptoProcessor("792c3be2e2607290ddb07ad8864a72ea33920bd12307b1dc08930f0431ed3921",exchange.transactionsList);

		processor.initTransactions();
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

	}

}
