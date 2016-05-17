/* Termo de licensa Apache Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package com.apptec.dtvmanager;

//Bibliotecas
/*Bibliotecas Android*/
import android.app.Activity;
import android.util.Log;
import android.view.Window;

/*Bibliotecas cordova*/
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

/*Bibliotecas Java*/

import java.util.List;
import java.lang.*;//Thread.*;

/*Bibliotecas SDMV - AIDL - low level*/
import com.sdmc.aidl.ProgramParcel;
import com.sdmc.aidl.SearchParcel;

/*### Bibliotecas SDMV - ACPI - high level ###*/
import com.sdmc.dtv.acpi.DTVACPIManager;
import com.sdmc.dtv.acpi.ProgramInfo;
import com.sdmc.dtv.acpi.ProgramSearch;
import com.sdmc.dtv.acpi.TunerInfo;
//import com.sdmc.dtv.acpi.QuickIntegration;

public class DTVManager extends CordovaPlugin {
    private static final String TAG = "DTVChannelSearch";
	//final Activity activityX = this.cordova.getActivity();
	
	/*Para instanciar, DTVACPIManager tem que ser inicializado*/
	private ProgramInfo piProgramInfo;
	private ProgramSearch psProgramSearch;
	private TunerInfo tiTunerInfo;
	private SearchParcel spSearchParcel;
	private SearchParcel spEndSearchParcel;
	private ProgramParcel ppInfoCurrentProgram;
	//private QuickIntegration mQuickIntegration;
	/*Requer level 3 para ser acessado*/
	//private DTVInfo mDTVInfo;
	
	/*Licença SDMC DTV*/
	private static final String LICENSE = "4BC3EE167DAE1AE42FA0C6712F73D3199ADE8C2A242A60FC1815E56E1C6E5B3D003F217FAE62473BBD21B671D42E2A0A447B1A5C48EC1AD72B807AC9E69F5385";
	
	/*Licensa não válida para teste*/
	//private static final String LICENSE = "license";
	
	/*DTVACPIManager não inicializado*/
	private boolean bInitSuccess = false;
	private int iLevel = 0;
	private boolean bStopSearch = false;
	private int iCurrFreqSearch = 0;
	
	/*Para pegar excessões do método: */
	/*private DTVACPIManager.OnInitCompleteListener mOnInitFinishListener*/	
	/*Motivo: fora do método exec*/
	private String sTest = " - sTest Não alterado";

	/* Inicialização do plugin
	* Sets the context of the Command. This can then be used to do things like
	* get file paths associated with the Activity.
	*
	* @param cordova The context of the main Activity.
	* @param webView The CordovaWebView Cordova is running in.
	*/
    @Override
    public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
        Log.v(TAG, "DTVChannelSearch: initialization");
        super.initialize(cordova, webView);
    }

    /* Executa a ação requerida e retorna Pluginresult     
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false otherwise.
     */
    @Override
    public boolean execute(final String action, final CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        //Log.v(TAG, "Executing action: " + action);
        final Activity activity = this.cordova.getActivity();
        final Window window = activity.getWindow();

		//Inicialização do DTVACPIManager
		//Escopo do método:
		//
		//public static boolean init(android.content.Context context,
		//						   java.lang.String license,
		//						   DTVACPIManager.OnInitCompleteListener listener)
		//
		//context -
		//license - string da licença
		//listener - listener completo da inicialização - callback???
		//
		//##############################################
		//Escopo do método DTVACPIManager.OnInitCompleteListener:
		//Obs.: Será chamado após completar a inicialização de DTVInterfaceManager
		//
		//void onInitComplete(boolean isInitSuccess, int level)

		//Inicia DTVACPIManager

		if("initDTVACPIManager".equals(action)) {
			DTVACPIManager.init(activity, LICENSE, mOnInitFinishListener);
			//Falta colocar timeout
			while(bInitSuccess == false){
				Thread.yield();
			}
			PluginResult prResult = new PluginResult(PluginResult.Status.NO_RESULT);
			prResult = new PluginResult(PluginResult.Status.OK,
										"<br><b>DTVACPIManager Initialized</b>" +
										"<br>sTest = " + sTest +
										"<br>InitSuccess = " + bInitSuccess +
										"<br>Access Level: " + iLevel);
			prResult.setKeepCallback(true);
			callbackContext.sendPluginResult(prResult);
			return true;
		}

		if("stopScanDTVChannels".equals(action)) {
			bStopSearch = true;
			return true;
		}

		//Ação do plugin - parâmetro "action"
		if("getNumberOfChannels".equals(action)) {

			PluginResult prResultNbr = new PluginResult(PluginResult.Status.NO_RESULT);

			try{
				piProgramInfo = new ProgramInfo();
				//List<ProgramParcel> ppAllChannels;
				//ppAllChannels = piProgramInfo.getPrograms();

				prResultNbr = new PluginResult(PluginResult.Status.OK,"<br>Number Of Channels = " + piProgramInfo.getPrograms().size());
				prResultNbr.setKeepCallback(true);
				callbackContext.sendPluginResult(prResultNbr);
				return true;

			} catch (Exception e){
				prResultNbr = new PluginResult(PluginResult.Status.OK,"ERROR IN JAVA (getNumberOfChannels) ");
				prResultNbr.setKeepCallback(true);
				callbackContext.sendPluginResult(prResultNbr);
				return true;
			}
		}

		//Ação do plugin - parâmetro "action"
		if("showAllChannels".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					PluginResult prResultSAC = new PluginResult(PluginResult.Status.NO_RESULT);

					try {
						piProgramInfo = new ProgramInfo();
                        //List<ProgramParcel> ppShowAllChannels;
						//ppShowAllChannels = piProgramInfo.getPrograms();

                        if(piProgramInfo.getPrograms().size() == 0){
                            prResultSAC = new PluginResult(PluginResult.Status.OK,"No channels");
                            prResultSAC.setKeepCallback(true);
                            callbackContext.sendPluginResult(prResultSAC);
                            return;
                        }

						int iNbrChannelsX = piProgramInfo.getPrograms().size();
                        for (int y = 0; iNbrChannelsX > y; y++) {
                            prResultSAC = new PluginResult(PluginResult.Status.OK,
                                    "Channels ID: " + piProgramInfo.getPrograms().get(y).getId() +
                                            " - Channels Number: " + piProgramInfo.getPrograms().get(y).getProgramNumber() +
                                            " - Channels Name: " + piProgramInfo.getPrograms().get(y).getName());
                            prResultSAC.setKeepCallback(true);
                            callbackContext.sendPluginResult(prResultSAC);
                        }
                        /*
						for (int y = 0; iNbrChannelsX > y; y++) {
							prResultSAC = new PluginResult(PluginResult.Status.OK,
											"Channels ID: " + ppShowAllChannels.get(y).getId() +
											" - Channels Number: " + ppShowAllChannels.get(y).getProgramNumber() +
											" - Channels Name: " + ppShowAllChannels.get(y).getName());
							prResultSAC.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultSAC);
						}*/

						//List<ProgramParcel> ppAllChannels;
						//ppAllChannels = piProgramInfo.getPrograms();
					} catch (Exception e) {
						prResultSAC = new PluginResult(PluginResult.Status.OK, "ERROR IN JAVA (showAllChannels) ");
						prResultSAC.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultSAC);
					}
				}
			});
			return true;
		}

		//Ação do plugin - parâmetro "action"
		if("getAllChannelsID".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					PluginResult prResultGACID = new PluginResult(PluginResult.Status.NO_RESULT);
					String buffer = "";

					try {
						piProgramInfo = new ProgramInfo();

						if(piProgramInfo.getPrograms().size() == 0){
							prResultGACID = new PluginResult(PluginResult.Status.OK,0);
							prResultGACID.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultGACID);
							return;
						}

						int iNbrChannelsX = piProgramInfo.getPrograms().size();
						for (int y = 0; iNbrChannelsX > y; y++) {
							buffer +=  piProgramInfo.getPrograms().get(y).getId();
							if(y < iNbrChannelsX-1) {
								buffer += ",";
							}
						}
						prResultGACID = new PluginResult(PluginResult.Status.OK,buffer);
						prResultGACID.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGACID);
					} catch (Exception e) {
						prResultGACID = new PluginResult(PluginResult.Status.OK, "ERROR IN JAVA (showAllChannels) ");
						prResultGACID.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGACID);
					}
				}
			});
			return true;
		}

		//Ação do plugin - parâmetro "action"
		if("deleteAllPrograms".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					boolean bIsDeleted;
					String bufferX;

					PluginResult prResultDel = new PluginResult(PluginResult.Status.NO_RESULT);

					try	{
						piProgramInfo = new ProgramInfo();
						List<ProgramParcel> ppAllChannelsX;
						int iNbrChannels = 0;
						int iInitialNbrOfChannels;
						int iCurrProgramID;


						try{
							iCurrProgramID = piProgramInfo.getCurrentProgram().getId();
							piProgramInfo.lockProgram(iCurrProgramID);
							prResultDel = new PluginResult(PluginResult.Status.OK,
									"<br>CurrProgram:  " + iCurrProgramID);
							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);
						} catch (Exception e){
							prResultDel = new PluginResult(PluginResult.Status.OK,
									"<br>No current program running");
							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);
						}

						ppAllChannelsX = piProgramInfo.getPrograms();
						iNbrChannels = ppAllChannelsX.size();
						iInitialNbrOfChannels = iNbrChannels;
						if (iNbrChannels > 0) {

							prResultDel = new PluginResult(PluginResult.Status.OK,
									"Deleting " + iInitialNbrOfChannels + " programs<br>");
							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);

                            int iID;

							for (int x = 0, j = 0; iInitialNbrOfChannels > x; ) {

                                iID = ppAllChannelsX.get(iNbrChannels-1).getId();
								bIsDeleted = piProgramInfo.deleteProgram(iID);
                                bufferX = "Deleted " + (j+1) + " programs --> ID: " + iID +
                                        //" --> Channel Name: " +ppAllChannels.get(x).getName() +
                                        " --> Nbr of Channels: " + (iNbrChannels - 1) +
                                        " --> piProgramInfo size: " + piProgramInfo.getPrograms().size() +
                                        " --> X = " + x +
                                        " - deleteProgramResult: ";
                                prResultDel = new PluginResult(PluginResult.Status.OK, bufferX + bIsDeleted);
                                prResultDel.setKeepCallback(true);
                                callbackContext.sendPluginResult(prResultDel);
                                iNbrChannels--;
                                x++;
                                j++;
							}
							prResultDel = new PluginResult(PluginResult.Status.OK, "<br>Number of programs after delete: " + piProgramInfo.getPrograms().size());
							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);
						} else {
							prResultDel = new PluginResult(PluginResult.Status.OK, "<br>No programs in database");
							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);
						}
					} catch (Exception e) {
						prResultDel = new PluginResult(PluginResult.Status.ERROR, "Error deleting programs");
						prResultDel.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultDel);
					}
				}
			});
			return true;
		}
		
		//Ação do plugin - parâmetro "action"
		if("scanDTVChannels".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {

					int iStartFrequency = 177143;
					int iEndFrequency = 803143;
					int iStartIndex = 1;
					int iEndIndex = 63;
					int iBandWidth = 6000;
					int iScanTime = 300000;

					//int iCurrentFrequency;
					//int iCurrentBandWidth;
					boolean bStatusAntena;
					long startTime = System.currentTimeMillis(); //fetch starting time
                    //searchListener slSearchListener = new searchListener();

					PluginResult prResultRun = new PluginResult(PluginResult.Status.NO_RESULT);

					//Callback mProgramSearch.setSearchListener(pslListener);
					ProgramSearch.ProgramSearchListener pslListener
							= new ProgramSearch.ProgramSearchListener() {

						PluginResult prResultListenerPS = new PluginResult(PluginResult.Status.NO_RESULT);
						//@Override
						public void onBeginOneFreq(final SearchParcel parcel) {
									try {
										prResultListenerPS = new PluginResult(PluginResult.Status.OK,
												"<br>##########PROGRAM SEARCH LISTENER#########<BR>" +
														"<br>Retorno callback (1) ProgramSearchListener onBeginOneFreq --> " +
														"<br><br>Parcel Frequency: " + parcel.getFrequency() +
														"<br><br>Parcel Modulation: " + parcel.getModulation() +
														"<br><br>Parcel BandWidth: " + parcel.getBandWidth() +
														"<br><br>Parcel SymbolRate: " + parcel.getSymbolRate() +
														"<br><br>Parcel Describe Contents: " + parcel.describeContents()
										);
										prResultListenerPS.setKeepCallback(true);
										callbackContext.sendPluginResult(prResultListenerPS);
									} catch (Exception e) {
										prResultListenerPS = new PluginResult(PluginResult.Status.ERROR,
												"<br>##########PROGRAM SEARCH LISTENER#########<BR>" +
														"<br>Retorno callback ERROR (1) ProgramSearchListener onBeginOneFreq --> " +
														"Exception: " + e);
										prResultListenerPS.setKeepCallback(true);
										callbackContext.sendPluginResult(prResultListenerPS);
									}
						}
						//@Override
						public void onEndOneFreq(final int currentFreqIndex, final int FreqCount, final ProgramParcel[] parcels) {

									try {
										prResultListenerPS = new PluginResult(PluginResult.Status.OK,
												"<br>##########PROGRAM SEARCH LISTENER#########<BR>" +
														"<br>Retorno callback (2) ProgramSearchListener onEndOneFreq --> " +
														"currentFreqIndex: " + currentFreqIndex + " - " +
														"FreqCount: " + FreqCount + " - " +
														"parcel length: " + parcels.length +
														"iCurrFreqRodrigo: " + (iCurrFreqSearch + 1)
										);
										iCurrFreqSearch++;
										prResultListenerPS.setKeepCallback(true);
										callbackContext.sendPluginResult(prResultListenerPS);


									} catch (Exception e) {
										prResultListenerPS = new PluginResult(PluginResult.Status.ERROR,
												"<br>##########PROGRAM SEARCH LISTENER#########<BR>" +
														"Retorno callback ERROR (1) ProgramSearchListener onBeginOneFreq --> " +
														"Exception: " + e);
										prResultListenerPS.setKeepCallback(true);
										callbackContext.sendPluginResult(prResultListenerPS);
									}
						}

					};
					//End Callback private ProgramSearch.ProgramSearchListener pslListener = new ProgramSearch.ProgramSearchListener()

					try {

						tiTunerInfo = new TunerInfo();
						piProgramInfo = new ProgramInfo();
						List<ProgramParcel> ppAllChannels;
						List<SearchParcel> getSearchParcelLists;
						spSearchParcel = new SearchParcel(iStartFrequency, iBandWidth);
						spEndSearchParcel = new SearchParcel(iEndFrequency, iBandWidth);
						psProgramSearch = new ProgramSearch();
						//ppInfoCurrentProgram = piProgramInfo.getCurrentProgram();

						bStatusAntena = tiTunerInfo.getAntennaPowerOnOff();
						if (!bStatusAntena) {
							tiTunerInfo.setAntennaPowerOnOff(true);
							bStatusAntena = tiTunerInfo.getAntennaPowerOnOff();
						}

						/*iCurrentFrequency = spSearchParcel.getFrequency();
						iCurrentBandWidth = spSearchParcel.getBandWidth();*/

						prResultRun = new PluginResult(PluginResult.Status.OK,
													  "<br>########## START SEARCHING - TIME TO WAITING SCAN: " + (iScanTime/60000) + " minutes #########<br>");
						prResultRun.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultRun);

						prResultRun = new PluginResult(PluginResult.Status.OK,
								"<br>Antena On: " + bStatusAntena + "<br>");
						prResultRun.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultRun);

						ppAllChannels = piProgramInfo.getPrograms();

						psProgramSearch.setSearchListener(pslListener);
						//psProgramSearch.autoSearchByIndex(iStartIndex,iEndIndex);
						psProgramSearch.autoSearch(spSearchParcel, spEndSearchParcel);

						long startScanTime = System.currentTimeMillis();

						int i = 0;
						int j = 1;
						String buffer;
						int iIntervalNothingSearch = 500;
						int iIntervalNothingSearchExcep = 10000;
						boolean bIsDivisibleBy3;

						//while ((System.currentTimeMillis() - startTime) < iScanTime) {
						while (iCurrFreqSearch < iEndIndex) {
							if(bStopSearch){
								psProgramSearch.stopAutoSearch();
								break;
							}
							Thread.yield();
							/*try {

								getSearchParcelLists = psProgramSearch.getSearchParcelList();
								if (psProgramSearch.getSearchParcelList().size()>0 && psProgramSearch.getSearchParcelList().size()>i){
									buffer = "<br>Channel Frequency: " + getSearchParcelLists.get(i).getFrequency() +
											" - Channel Modulation: " + getSearchParcelLists.get(i).getModulation() +
											//" - ID: " + ppAllChannels.get(i).getId() +
											 " - Elapsed Time: " + (System.currentTimeMillis() - startScanTime);
									prResultRun = new PluginResult(PluginResult.Status.OK,buffer);
									prResultRun.setKeepCallback(true);
									callbackContext.sendPluginResult(prResultRun);
									i++;
								}
								if(!((System.currentTimeMillis() - startTime) < iIntervalNothingSearch)) {
									//ppAllChannels = piProgramInfo.getPrograms();
									//iCurrentFrequency = spEndSearchParcel.getFrequency();
									//iCurrentFrequency = psProgramSearch.getSearchParcelList().get(i).getFrequency();
									bIsDivisibleBy3 = (j-1) % 3 == 0;
									if(bIsDivisibleBy3){
										buffer = "<br> * " + j + " - Searching - i = " + i +
												" - Number of Channels = " + piProgramInfo.getPrograms().size() +
												//" - Current Frequency: " + iCurrentFrequency + "<br>" +
												" - Elapsed Time: " + ((int) ((System.currentTimeMillis() - startScanTime) / 1000) + 1) + " seconds";
									} else {
										buffer = " * " + j + " - Searching - i = " + i +
												//" - Number of Programs = " + psProgramSearch.getSearchParcelList().size() +
												//" - Current Frequency: " + iCurrentFrequency + "<br>" +
												" - Elapsed Time: " + ((int) ((System.currentTimeMillis() - startScanTime) / 1000) + 1) + " seconds";
									}
									prResultRun = new PluginResult(PluginResult.Status.OK, buffer);
									prResultRun.setKeepCallback(true);
									callbackContext.sendPluginResult(prResultRun);
									iIntervalNothingSearch += 10000;
									j++;

								Thread.yield();
							} catch (Exception e){
								if(!((System.currentTimeMillis() - startTime) < iIntervalNothingSearchExcep)) {
									prResultRun = new PluginResult(PluginResult.Status.OK, "<br>Excep - Searching - ");
									prResultRun.setKeepCallback(true);
									callbackContext.sendPluginResult(prResultRun);
									iIntervalNothingSearchExcep += 10000;
								}
								Thread.yield();
							}*/
							//Thread.yield();
						}
						if(!bStopSearch) {
							psProgramSearch.stopAutoSearch();
						}

						bStopSearch = false;
						iCurrFreqSearch = 0;
						ppAllChannels = piProgramInfo.getPrograms();
						//iCurrentFrequency = spEndSearchParcel.getFrequency();


						prResultRun = new PluginResult(PluginResult.Status.OK,
								"<br><br><b>SCAN CHANNELS SUMMARY</b>" +
										"<br><br>DTVACPIManager iniciado: " + bInitSuccess +
										"<br>Access Level: " + iLevel +
										"<br><br><<< <b>Current channel info</b> >>>" +
										"<br><br>Nº of Channels: " + ppAllChannels.size() +
										"<br><br><<< <b>Frequency, BandWidth & Antena Status Info</b> >>>" +
										//"<br><br>Frequência: " + iCurrentFrequency +
										//"<br>Larg. Banda: " + iCurrentBandWidth + "KHz" +
										"<br>Status Antena: " + bStatusAntena +
										"<br>Elapsed Time: " + (System.currentTimeMillis() - startTime) + "ms" +
										"<br>sTest = " + sTest +
										"<br>Activity: " + activity
						);

						prResultRun.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultRun);
						tiTunerInfo.setAntennaPowerOnOff(false);
						//DTVACPIManager.release();
						//pslListener.onBeginOneFreq(mSearchParcel);

						//return;
					} catch (Exception e) {
						//Callback Erro
						callbackContext.sendPluginResult(
								new PluginResult(PluginResult.Status.ERROR,
												"CALLBACK ERROR ACIONADO >>><<< MÓDULO JAVA >>><<< MÉTODO public boolean execute >>><<<" +
												"EXCEPTION (exec + onInitComplete): " + e +
												"<br>License OK: " + bInitSuccess +
												"<br>Exception (DTVACPIManager)" + sTest
								)
						);
						//return;

					}

					//Métodos testados funcionando - usados para testes

					//Muda o canal
					//mProgramInfo.playProgram(3);

					//Get Search frequency list
					//List<SearchParcel> lFreqList = mProgramSearch.getSearchParcelList();

					//getStreamTime - verificar o que é
					//String sStreamTime = mProgramInfo.getStreamTime();

					//Inicia o módulo para buscar canais digitais pela antena
					//mQuickIntegration.startSearchActivity();

					//Pega programa corrente e próximo do EPG
					//List<String> lsPFEPG = mProgramInfo.getPFEPG();

					//Pega programação de hoje e dos próximos 6 dias
					//List<String> lsTodayEPG = mProgramInfo.getEPG(0);
					//List<String> lsTomorrowEPG = mProgramInfo.getEPG(1);

					//Pega informações do programa corrente
					//ProgramParcel mInfoCurrentProgram = mProgramInfo.getCurrentProgram();

					//Status do sinal do programa corrente
					//int[] iaSignalStatus = mTunerInfo.getSignalStatus();

					//Precisa testar essa forma de pegar o nome do canal
					//String sName = ProgramParcel.getName();

					//Fim métodos testados

					//Métodos retornando erro
					//Requer level 3 - não acessível
					//boolean bIsAPPInit = mDTVInfo.isAPPInit();
					//String sGetTunerType = mDTVInfo.getTunerType();

				}//End public void Run() - getThreadPool				

			});	//End getThreadPool
			return true;
		}// End if("getBandWidth".equals(action))

		//Ação do plugin - parâmetro "action"
		if("getCurrProgram".equals(action)) {
			cordova.getThreadPool().execute(new Runnable(){
				public void run(){

					PluginResult prResultDel = new PluginResult(PluginResult.Status.NO_RESULT);
					try{
						piProgramInfo = new ProgramInfo();
						if(piProgramInfo.getPrograms().size() == 0){
							prResultDel = new PluginResult(PluginResult.Status.OK, "<br>No programs in database");

							prResultDel.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultDel);
						}

						prResultDel = new PluginResult(PluginResult.Status.OK,
														"<br>Current Playing Channel Info:" +
														"<br><br>ID: " + piProgramInfo.getCurrentProgram().getId() +
														"<br>Channel Number: " + piProgramInfo.getCurrentProgram().getProgramNumber() +
														"<br>Channel Name: " +piProgramInfo.getCurrentProgram().getName()
														);

						prResultDel.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultDel);

					} catch (Exception e){

						prResultDel = new PluginResult(PluginResult.Status.OK,
								"<br>No current program running");
						prResultDel.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultDel);
					}
				}//End public void Run() - getThreadPool
			});	//End getThreadPool
			return true;
		}// End if("getCurrProgram".equals(action))

		//Ação do plugin - parâmetro "action"
		if("getEPGByDayOfTheWeekIndex".equals(action)) {
			cordova.getThreadPool().execute(new Runnable(){
				public void run(){

					PluginResult prResultGetEPGByDay;
					try{
						piProgramInfo = new ProgramInfo();
						if(piProgramInfo.getPrograms().size() == 0){
							prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK, "<br>No programs in database");

							prResultGetEPGByDay.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultGetEPGByDay);
							return;
						}

						if(args.isNull(0)){
							prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK,
									"<br>Need to pass an day index to get the EPG (0 to 6)<br>Current Day = 0, Tomorrow = 1, ..., Last EPG Day = 6");
							prResultGetEPGByDay.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultGetEPGByDay);
							return;
						}

						prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK,
										"<br>EPG of day with index: " + args.getInt(0) +
										"<br><br>EPG: " + piProgramInfo.getEPG(args.getInt(0))
						);

						prResultGetEPGByDay.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGetEPGByDay);

					} catch (Exception e){

						prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK,
								"<br>No current program running. Need to play program to get EPG");
						prResultGetEPGByDay.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGetEPGByDay);
					}
				}//End public void Run() - getThreadPool
			});	//End getThreadPool
			return true;

		}// End if("getAllPrograms".equals(action))

		//Ação do plugin - parâmetro "action"
		if ("getEPGCurrNext".equals(action)) {
			cordova.getThreadPool().execute(new Runnable(){
				public void run(){

					PluginResult prResultGetEPGByDay;
					try{
						piProgramInfo = new ProgramInfo();
						if(piProgramInfo.getPrograms().size() == 0){
							prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK, "<br>No programs in database");

							prResultGetEPGByDay.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultGetEPGByDay);
							return;
						}

						prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK,
										"<br>EPG: " + piProgramInfo.getPFEPG()
						);

						prResultGetEPGByDay.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGetEPGByDay);

					} catch (Exception e){

						prResultGetEPGByDay = new PluginResult(PluginResult.Status.OK,
								"<br>No current program running. Need to play program to get EPG");
						prResultGetEPGByDay.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGetEPGByDay);
					}
				}//End public void Run() - getThreadPool
			});	//End getThreadPool
			return true;

		}// End if("getAllPrograms".equals(action))

		//Ação do plugin - parâmetro "action"
		if ("getSearchedFreqList".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {

					PluginResult prResultGSFL = new PluginResult(PluginResult.Status.NO_RESULT);
					try {
						int iNumberOfSearchedFrequencies;
						psProgramSearch = new ProgramSearch();
						List<SearchParcel> getSearchParcelLists;
						getSearchParcelLists = psProgramSearch.getSearchParcelList();
						iNumberOfSearchedFrequencies = getSearchParcelLists.size();

						prResultGSFL = new PluginResult(PluginResult.Status.OK,
								"<br>Number of Searched Frequencies: " + getSearchParcelLists.size() + "<br>");
						prResultGSFL.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGSFL);

						if (iNumberOfSearchedFrequencies == 0) {
							return;
						}

						for (int i = 0; iNumberOfSearchedFrequencies > i; i++) {
							prResultGSFL = new PluginResult(PluginResult.Status.OK,
									"<br>Frequency " + i + " - " + getSearchParcelLists.get(i).getFrequency());
							prResultGSFL.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultGSFL);
						}


					} catch (Exception e){
						prResultGSFL = new PluginResult(PluginResult.Status.OK,
								"<br>ERROR GETTING SEARCHED FREQUENCY LIST");
						prResultGSFL.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultGSFL);
					}
				}//End public void Run() - getThreadPool
			});	//End getThreadPool
			return true;
		}// End if("getAllPrograms".equals(action))

		//Ação do plugin - parâmetro "action"
		if("playChannelByID".equals(action)) {
			//cordova.getThreadPool().execute(new Runnable(){
			//	public void run(){

					PluginResult prResultPCId;
					boolean b = true;
					try{
						//String sChannelID = args.get(0).toString();
						piProgramInfo = new ProgramInfo();
						int iNumberOfChannels = piProgramInfo.getPrograms().size();

						if(iNumberOfChannels == 0){
							prResultPCId = new PluginResult(PluginResult.Status.OK,
									"<br>No channels in database");
							prResultPCId.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultPCId);
							return true;
						}

						if(args.isNull(0)){
							prResultPCId = new PluginResult(PluginResult.Status.OK,
									"<br>Need to pass an ID Channel to play");
							prResultPCId.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultPCId);
							return true;
						}

						int iChannelID = Integer.parseInt(args.get(0).toString());
						int iCurrPlayingChannel = piProgramInfo.getCurrentProgram().getId();

						if(iChannelID == iCurrPlayingChannel) {
							prResultPCId = new PluginResult(PluginResult.Status.OK,
									"Already playing this channel - ID"	+ iCurrPlayingChannel);

							prResultPCId.setKeepCallback(true);
							callbackContext.sendPluginResult(prResultPCId);
							return true;
						}


						for(int i = 0; i < iNumberOfChannels; i++){
							if(iChannelID == piProgramInfo.getPrograms().get(i).getId()){
								piProgramInfo.playProgram(iChannelID);
								prResultPCId = new PluginResult(PluginResult.Status.OK,
										"<br>Playing Channel ID: " + iChannelID);
								prResultPCId.setKeepCallback(true);
								callbackContext.sendPluginResult(prResultPCId);
								return true;
							}
						}

						prResultPCId = new PluginResult(PluginResult.Status.OK,	"<br>Invalid ID");
						prResultPCId.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultPCId);

					} catch (Exception e){
						prResultPCId = new PluginResult(PluginResult.Status.OK,
								"<br>ERROR PLAYING CHANNEL");
						prResultPCId.setKeepCallback(true);
						callbackContext.sendPluginResult(prResultPCId);
						return true;
					}
				//}//End public void Run() - getThreadPool
			//});	//End getThreadPool
			return true;
		}// End if("getAllPrograms".equals(action))

		//Ação do plugin - parâmetro "action"
		if("stopDTVACPIManager".equals(action)) {

			PluginResult prResult;

			if(!bInitSuccess){
				prResult = new PluginResult(PluginResult.Status.OK,
						"<br><b>DTVACPIManager não inicializado. Não há necessidade de pará-lo</b>" +
								"<br>InitSuccess = " + bInitSuccess +
								"<br>Access Level: " + iLevel);
				prResult.setKeepCallback(true);
				callbackContext.sendPluginResult(prResult);
				return true;
			}

			DTVACPIManager.release();

			bInitSuccess = false;
			iLevel = 0;

			prResult = new PluginResult(PluginResult.Status.OK,
					"<br><b>DTVACPIManager Stopped</b>" +
							"<br>InitSuccess = " + bInitSuccess +
							"<br>Access Level: " + iLevel);
			prResult.setKeepCallback(true);
			callbackContext.sendPluginResult(prResult);
			return true;

		}// End if("getAllPrograms".equals(action))

		//If action not equals any planned actions, return false - Invalid action
        return false;
    }// End public boolean execute
	
	//Callback inicialização DTVACPIManager
	private DTVACPIManager.OnInitCompleteListener mOnInitFinishListener = new DTVACPIManager.OnInitCompleteListener() {
		@Override
		public void onInitComplete(boolean isSuccess, int level) {
			
			sTest = "After onInitComplete <<<>>> Before try <<<>>> InitStatus (isSuccess) = " + isSuccess;
			iLevel = level;
			bInitSuccess = isSuccess;
			
			/*if (isSuccess) {
				sTest = "onInitComplete >>> Callback OK >>> Licença OK >>> " + bInitSuccess;
			} else {
				sTest = "onInitComplete >>> Callback OK >>> Licença INVÁLIDA >>> " + bInitSuccess;
			}*/
		}		
	};
	//End Callback inicialização DTVACPIManager*/
}