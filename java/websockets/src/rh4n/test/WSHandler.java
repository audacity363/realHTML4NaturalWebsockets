package rh4n.test;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import realHTML.JSONConverter.JSONConverter;
import realHTML.jni.ChildProcess;
import realHTML.jni.JNI;
import realHTML.jni.exceptions.NoClientException;
import realHTML.jni.natural.ChildInformations;
import realHTML.jni.natural.Message;
import realHTML.jni.natural.MessageType;
import realHTML.tomcat.routing.Route;

import java.io.EOFException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class WSHandler extends Endpoint {

	JNI jnihandler;
	
	public WSHandler() {
		System.out.println("Init WSHandler");
		
	}
	
	private class WSThread extends Thread {
		
		Set<Session> clients;
		Boolean exit = false;
		
		Queue<Message> queue;
		
		public WSThread(Set<Session> clients) {
			this.clients = clients;
			this.queue = new LinkedList<Message>();
		}

		public void run() {
			while(true) {
				try {
					Thread.sleep(10);
					if(this.exit) {
						break;
					}
					
					//That is what I have to implement
//					if(natmsg.type == getSize) {
//						write(writePipe, getQueueSize())
//					} else if(natmsg.type == getMsg) {
//						if(queueLength == 0 && natmsg.waitForMsg) {
//							startTime = getCurrentDateTime()
//							while(true) {
//								if(!queue.isEmpty()) {
//									write(writePipe, popMsgfromQueue)
//									break;
//								}
//								if(natmsg.waitForSeconds != -1) {
//									currentTime = getCurrentDateTime()
//									if(currentTime - startTime >= natmsg.waitForSeconds) {
//										//Timeout
//										write(writePipe, TIMEOUT)
//										break;
//									}
//								}Loading so with Library
//								sleep(10)
//							}
//						} else {
//							write(writePipe, popMsgfromQueue)	
//						}
//					} else if(natmsg.type == clearQueue) {
//						queue.clear()
//					} else if(natmsg.type == closeConnection) {
//						socket.close()
//						break;
//					}
					
					if(!this.queue.isEmpty()) {
						Message target = this.queue.poll();
						synchronized (this.clients) {
							for(Session client: this.clients) {
								client.getBasicRemote().sendText("Got message from Type: " + target.type);
							}
						}
					}
				} catch (InterruptedException e) {
					System.out.println("God an interrupt");
					if(this.exit) {
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			System.out.println("Exiting sending thread");
			
			//Close Pipes and write shutdown message to pipe
		}
		
		public void stopSend() {
			this.exit = true;
		}
		
		public synchronized void addMessage(Message msg) {
			this.queue.add(msg);
		}

	}
	
	private class WSCollection {
		WSThread sendingThread;
		int serverHandle = -1;
		java.util.Set<Session> clients;
	}
	
	
	static Map<Integer, WSCollection> activeWSs =  
			Collections.synchronizedMap(new HashMap<Integer, WSCollection>());

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		System.out.println("User connected");
		
		System.out.println("new JNI");
		this.jnihandler = new JNI();
		System.out.println("Hopfully the jni is now loaded");
		
		System.out.println("Print URL Variables");
		for(Map.Entry<String, List<String>> entry: session.getRequestParameterMap().entrySet()) {
			System.out.println(entry.getKey() + " - " + entry.getValue().toString());
		}
		
		System.out.println("End-Print Variables");
		
		Route targetroute = (Route)config.getUserProperties().get("routeconfig");
		System.out.println("Staring id: [" + targetroute.id + "] " + targetroute.natProgram + " in " + 
				targetroute.natLibrary);

		if(activeWSs.containsKey(targetroute.id)) {
			System.out.println("There is already a running thread");
			WSHandler.activeWSs.get(targetroute.id).clients.add(session);
		} else {
			System.out.println("First connection on that client");
			WSCollection current = new WSCollection();
			current.clients = Collections.synchronizedSet(new HashSet<Session>());
			current.clients.add(session);
			
			System.out.println("Starting Thread");
			current.sendingThread = new WSThread(current.clients);
			current.sendingThread.start();
			
			WSHandler.activeWSs.put(targetroute.id, current);
			String filepath;
			int serverFD = -1 ;
			int timeout = 0;
			
			filepath = "/tmp/rh4nws_" + targetroute.id;
			serverFD = this.jnihandler.createNonBlockingUDS(filepath);
			System.out.printf("God new FD: [%d]\n", serverFD);
			if(serverFD == -1) {
				return;
			}
			
			int pid = this.jnihandler.startNaturalWS("/home/tom/Documents/Java/websockets/c", filepath);
			int clientFD = -1;
			
			do {
				try {
					clientFD = this.jnihandler.waitForClient(filepath, serverFD);
					System.out.printf("God new client: [%d]\n", clientFD);
				} catch(NoClientException e) {
					System.out.println("Client Timeout.");
					try {
						Thread.sleep(500);
					} catch(Exception e1) {
						
					}
					timeout += 1;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while(clientFD == -1 && timeout < 10);
			
			if(timeout >= 10) {
				System.out.println("No Client after 5 seconds");
				ChildProcess natchild = this.jnihandler.getChildProcessStatus(pid);
				System.out.println(natchild);
				if(!natchild.exited) {
					this.jnihandler.killChildProcess(natchild.pid, 9);
					return;
				}
			}
			
			System.out.println("Send Session infos to child");
			Message msg = new Message();
			msg.type = MessageType.CHILDINFORMATIONS;
			msg.msg = new ChildInformations("testlib", "testprog", "etid=$$ parm=logiparm", "/tmp/nat/", "DEBUG", "/tmp/log/");
			try {
				this.jnihandler.sendMessageToNatural(clientFD, msg);
			} catch(Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
		
		/*
        if(First client on that route) {

		    //Create UDS Server (Non Blocking)
		    
		    //Create Natural process but don't wait for natural to exit

            //Poll for a client
            
            while(client == -1) {
            int client = -1, timeout = 0;
                if((client = pollClientFromUDSServer()) != -1) {
                    break;
                }op 
                sleep(10)
                timeout += 10;
                //Timeout with five seconds
                if(timeout >= 5000) {
                    break;
                }
            }
            if(timeout >= 5000) {
                Disconnect client
                kill natural process with SIGKILL
                shutdown UDS Server
                return;
            }
            create send thread. 
        } else {
            Add client to running websocket
            notify Natural over the establised connection
        }

        Send our client information to natural
        */
		
		
		session.addMessageHandler(new MessageHandler.Whole<Object>() {
			
			@Override
			public void onMessage(Object msg) {
				Message newmsg = new Message();
				if(msg instanceof JSONConverter) {
					try {
						newmsg.msg =  ((JSONConverter)msg).parse();
						newmsg.type = MessageType.JSON;
					} catch (Exception e) {
						//JSON Parsing failed. Write Error Message to client
						e.printStackTrace();
						try {
							session.getBasicRemote().sendText("Error: " + e.getMessage());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}	
				} else {
					newmsg.msg = msg;
					newmsg.type = MessageType.TEXT;
				}
				
				synchronized (WSHandler.activeWSs.get(targetroute.id).sendingThread) {
					WSHandler.activeWSs.get(targetroute.id).sendingThread.addMessage(newmsg);	
				}
			}
		});
	}
	
	@Override
	public void onClose(Session session, CloseReason reason) {
		
		this.removeSessionfromPool(session);
	}
	
	@Override
	public void onError(Session session, Throwable error) {
		if (error instanceof EOFException) {
			System.out.println("Client disconnected");
		} else {
			System.out.println("Something bad has happen:");
			new Exception(error).printStackTrace();
		}
	}
	
	void removeSessionfromPool(Session session) {
		synchronized (WSHandler.activeWSs) {
			Integer id = 0;
			WSCollection target = null;
			
			for(Map.Entry<Integer, WSCollection> entry: WSHandler.activeWSs.entrySet()) {
				if(entry.getValue().clients.contains(session)) {
					System.out.println("delete Session from pool");
					entry.getValue().clients.remove(session);
					id = entry.getKey();
					target = entry.getValue();
					break;
				}
			}
			
			if(target.clients.size() == 0) {
				System.out.println("No client left. Clearing handler");
				target.sendingThread.stopSend();
				WSHandler.activeWSs.remove(id, target);
			}
			
		}
	}
}
