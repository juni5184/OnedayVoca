package com.example.onedayvoca.Token;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 * <p>
 * <p>Generated with web3j version 3.5.0.
 */
public class JuniToken extends Contract {
    private static final String BINARY = "0x60806040526004361061004b5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166370a082318114610050578063a9059cbb14610090575b600080fd5b34801561005c57600080fd5b5061007e73ffffffffffffffffffffffffffffffffffffffff600435166100d5565b60408051918252519081900360200190f35b34801561009c57600080fd5b506100c173ffffffffffffffffffffffffffffffffffffffff600435166024356100e7565b604080519115158252519081900360200190f35b60006020819052908152604090205481565b3360009081526020819052604081205482111561010357600080fd5b73ffffffffffffffffffffffffffffffffffffffff8316600090815260208190526040902054828101101561013757600080fd5b50336000908152602081905260408082208054849003905573ffffffffffffffffffffffffffffffffffffffff8416825290208054820190556001929150505600a165627a7a7230582002336e2cd9461e4cafdda2ee5037db66512d2ba72a01ae74ab36c415c8234d460029\n";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_ALLOCATETOKEN = "allocateToken";

    public static final String FUNC_ISFUNDING = "isFunding";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_TOKENRAISED = "tokenRaised";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_NEWCONTRACTADDR = "newContractAddr";

    public static final String FUNC_TOKENEXCHANGERATE = "tokenExchangeRate";

    public static final String FUNC_STOPFUNDING = "stopFunding";

    public static final String FUNC_SETMIGRATECONTRACT = "setMigrateContract";

    public static final String FUNC_VERSION = "version";

    public static final String FUNC_TOKENMIGRATED = "tokenMigrated";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_CURRENTSUPPLY = "currentSupply";

    public static final String FUNC_STARTFUNDING = "startFunding";

    public static final String FUNC_MIGRATE = "migrate";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_DECREASESUPPLY = "decreaseSupply";

    public static final String FUNC_CHANGEOWNER = "changeOwner";

    public static final String FUNC_ETHFUNDDEPOSIT = "ethFundDeposit";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_INCREASESUPPLY = "increaseSupply";

    public static final String FUNC_SETTOKENEXCHANGERATE = "setTokenExchangeRate";

    public static final String FUNC_FUNDINGSTARTBLOCK = "fundingStartBlock";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_TRANSFERETH = "transferETH";

    public static final String FUNC_FUNDINGSTOPBLOCK = "fundingStopBlock";

    public static final Event ALLOCATETOKEN_EVENT = new Event("AllocateToken",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }), Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));


    public static final Event ISSUETOKEN_EVENT = new Event("IssueToken",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }), Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
    }, new TypeReference<Uint256>() {
    }));


    public static final Event INCREASESUPPLY_EVENT = new Event("IncreaseSupply",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
            }), Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
    }, new TypeReference<Uint256>() {
    }));
    ;

    public static final Event DECREASESUPPLY_EVENT = new Event("DecreaseSupply",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
            }), Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
    }, new TypeReference<Uint256>() {
    }));
    ;

    public static final Event MIGRATE_EVENT = new Event("Migrate",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }), Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
    }, new TypeReference<Uint256>() {
    }));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }), Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
    }, new TypeReference<Uint256>() {
    }));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }), Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
    }, new TypeReference<Uint256>() {
    }));
    ;

    protected JuniToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected JuniToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final Function function = new Function(
                FUNC_APPROVE,
                Arrays.<Type>asList(new Address(_spender),
                        new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> allocateToken(String _addr, BigInteger _eth) {
        final Function function = new Function(
                FUNC_ALLOCATETOKEN,
                Arrays.<Type>asList(new Address(_addr),
                        new Uint256(_eth)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isFunding() {
        final Function function = new Function(FUNC_ISFUNDING,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFERFROM,
                Arrays.<Type>asList(new Address(_from),
                        new Address(_to),
                        new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> tokenRaised() {
        final Function function = new Function(FUNC_TOKENRAISED,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> newContractAddr() {
        final Function function = new Function(FUNC_NEWCONTRACTADDR,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> tokenExchangeRate() {
        final Function function = new Function(FUNC_TOKENEXCHANGERATE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> stopFunding() {
        final Function function = new Function(
                FUNC_STOPFUNDING,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setMigrateContract(String _newContractAddr) {
        final Function function = new Function(
                FUNC_SETMIGRATECONTRACT,
                Arrays.<Type>asList(new Address(_newContractAddr)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> version() {
        final Function function = new Function(FUNC_VERSION,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> tokenMigrated() {
        final Function function = new Function(FUNC_TOKENMIGRATED,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF,
                Arrays.<Type>asList(new Address(_owner)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> currentSupply() {
        final Function function = new Function(FUNC_CURRENTSUPPLY,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> startFunding(BigInteger _fundingStartBlock, BigInteger _fundingStopBlock) {
        final Function function = new Function(
                FUNC_STARTFUNDING,
                Arrays.<Type>asList(new Uint256(_fundingStartBlock),
                        new Uint256(_fundingStopBlock)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> migrate() {
        final Function function = new Function(
                FUNC_MIGRATE,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> decreaseSupply(BigInteger _value) {
        final Function function = new Function(
                FUNC_DECREASESUPPLY,
                Arrays.<Type>asList(new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> changeOwner(String _newFundDeposit) {
        final Function function = new Function(
                FUNC_CHANGEOWNER,
                Arrays.<Type>asList(new Address(_newFundDeposit)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> ethFundDeposit() {
        final Function function = new Function(FUNC_ETHFUNDDEPOSIT,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER,
                Arrays.<Type>asList(new Address(_to),
                        new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> increaseSupply(BigInteger _value) {
        final Function function = new Function(
                FUNC_INCREASESUPPLY,
                Arrays.<Type>asList(new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setTokenExchangeRate(BigInteger _tokenExchangeRate) {
        final Function function = new Function(
                FUNC_SETTOKENEXCHANGERATE,
                Arrays.<Type>asList(new Uint256(_tokenExchangeRate)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> fundingStartBlock() {
        final Function function = new Function(FUNC_FUNDINGSTARTBLOCK,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function(FUNC_ALLOWANCE,
                Arrays.<Type>asList(new Address(_owner),
                        new Address(_spender)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferETH() {
        final Function function = new Function(
                FUNC_TRANSFERETH,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> fundingStopBlock() {
        final Function function = new Function(FUNC_FUNDINGSTOPBLOCK,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<JuniToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _ethFundDeposit, BigInteger _currentSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_ethFundDeposit),
                new Uint256(_currentSupply)));
        return deployRemoteCall(JuniToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<JuniToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _ethFundDeposit, BigInteger _currentSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_ethFundDeposit),
                new Uint256(_currentSupply)));
        return deployRemoteCall(JuniToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<AllocateTokenEventResponse> getAllocateTokenEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(ALLOCATETOKEN_EVENT, transactionReceipt);
        ArrayList<AllocateTokenEventResponse> responses = new ArrayList<AllocateTokenEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            AllocateTokenEventResponse typedResponse = new AllocateTokenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<AllocateTokenEventResponse> allocateTokenEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, AllocateTokenEventResponse>() {
            @Override
            public AllocateTokenEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(ALLOCATETOKEN_EVENT, log);
                AllocateTokenEventResponse typedResponse = new AllocateTokenEventResponse();
                typedResponse.log = log;
                typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<AllocateTokenEventResponse> allocateTokenEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ALLOCATETOKEN_EVENT));
        return allocateTokenEventObservable(filter);
    }

    public List<IssueTokenEventResponse> getIssueTokenEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(ISSUETOKEN_EVENT, transactionReceipt);
        ArrayList<IssueTokenEventResponse> responses = new ArrayList<IssueTokenEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            IssueTokenEventResponse typedResponse = new IssueTokenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<IssueTokenEventResponse> issueTokenEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, IssueTokenEventResponse>() {
            @Override
            public IssueTokenEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(ISSUETOKEN_EVENT, log);
                IssueTokenEventResponse typedResponse = new IssueTokenEventResponse();
                typedResponse.log = log;
                typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<IssueTokenEventResponse> issueTokenEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ISSUETOKEN_EVENT));
        return issueTokenEventObservable(filter);
    }

    public List<IncreaseSupplyEventResponse> getIncreaseSupplyEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(INCREASESUPPLY_EVENT, transactionReceipt);
        ArrayList<IncreaseSupplyEventResponse> responses = new ArrayList<IncreaseSupplyEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            IncreaseSupplyEventResponse typedResponse = new IncreaseSupplyEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<IncreaseSupplyEventResponse> increaseSupplyEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, IncreaseSupplyEventResponse>() {
            @Override
            public IncreaseSupplyEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(INCREASESUPPLY_EVENT, log);
                IncreaseSupplyEventResponse typedResponse = new IncreaseSupplyEventResponse();
                typedResponse.log = log;
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<IncreaseSupplyEventResponse> increaseSupplyEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INCREASESUPPLY_EVENT));
        return increaseSupplyEventObservable(filter);
    }

    public List<DecreaseSupplyEventResponse> getDecreaseSupplyEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(DECREASESUPPLY_EVENT, transactionReceipt);
        ArrayList<DecreaseSupplyEventResponse> responses = new ArrayList<DecreaseSupplyEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            DecreaseSupplyEventResponse typedResponse = new DecreaseSupplyEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<DecreaseSupplyEventResponse> decreaseSupplyEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, DecreaseSupplyEventResponse>() {
            @Override
            public DecreaseSupplyEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(DECREASESUPPLY_EVENT, log);
                DecreaseSupplyEventResponse typedResponse = new DecreaseSupplyEventResponse();
                typedResponse.log = log;
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<DecreaseSupplyEventResponse> decreaseSupplyEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DECREASESUPPLY_EVENT));
        return decreaseSupplyEventObservable(filter);
    }

    public List<MigrateEventResponse> getMigrateEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(MIGRATE_EVENT, transactionReceipt);
        ArrayList<MigrateEventResponse> responses = new ArrayList<MigrateEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            MigrateEventResponse typedResponse = new MigrateEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MigrateEventResponse> migrateEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MigrateEventResponse>() {
            @Override
            public MigrateEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(MIGRATE_EVENT, log);
                MigrateEventResponse typedResponse = new MigrateEventResponse();
                typedResponse.log = log;
                typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MigrateEventResponse> migrateEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MIGRATE_EVENT));
        return migrateEventObservable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventObservable(filter);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventObservable(filter);
    }

    public static JuniToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new JuniToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static JuniToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new JuniToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class AllocateTokenEventResponse {
        public Log log;

        public String _to;

        public BigInteger _value;
    }

    public static class IssueTokenEventResponse {
        public Log log;

        public String _to;

        public BigInteger _value;
    }

    public static class IncreaseSupplyEventResponse {
        public Log log;

        public BigInteger _value;
    }

    public static class DecreaseSupplyEventResponse {
        public Log log;

        public BigInteger _value;
    }

    public static class MigrateEventResponse {
        public Log log;

        public String _to;

        public BigInteger _value;
    }

    public static class TransferEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _value;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String _owner;

        public String _spender;

        public BigInteger _value;
    }
}
