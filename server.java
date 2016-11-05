import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.channels.*;
import java.nio.*;
import java.util.concurrent.*;
class Server 
{
	ArrayList<Socket> clientArray;
	ConcurrentLinkedQueue<String> msgQueue;
	Selector selector;
	public static void main(String args[])throws IOException
	{
		Server serve = new Server();
		
	}

	public Server()throws IOException
	{
		//this.listen();
		clientArray=new ArrayList<Socket>();
		msgQueue= new ConcurrentLinkedQueue<String>();
		selector = Selector.open();
		Thread t1 = new Thread(){
			public void run()
			{
				listen();
			}
		};
		Thread t2 = new Thread(){
			public void run()
			{
				try{
					receiveMessages();
				}
				catch(Exception e){}
			}
		};
		Thread t3 = new Thread(){
			public void run()
			{
				try
				{
					broadcast();
				}
				catch(Exception e){}
			}
		};
		//t1.start();
		t2.start();
		t3.start();
	}
	public void listen()
	{
		try
		{
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ServerSocket sock = ssc.socket();
     		InetSocketAddress isa = new InetSocketAddress(5555);
      		sock.bind(isa);
			while(true)
			{
				Socket client = sock.accept();
				System.out.println(client);
				synchronized(clientArray)
				{
					clientArray.add(client);
				}
				SocketChannel sc = client.getChannel();
				System.out.println(sc);
				sc.configureBlocking(false);
				selector.wakeup();
				sc.register(selector,SelectionKey.OP_READ);
				System.out.println("New client: "+client.getRemoteSocketAddress().toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void receiveMessages()
	{
		System.out.println("Server receiving messages now");
		ServerSocketChannel ssc;
		ServerSocket sock=null;
		try
		{
			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			sock = ssc.socket();
     		InetSocketAddress isa = new InetSocketAddress(5555);
      		sock.bind(isa);
      		ssc.register(selector,SelectionKey.OP_ACCEPT);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		while(true)
		{
			try
			{
				int num=selector.select();
				if(num==0)
					continue;
				Set keys = selector.selectedKeys();
				Iterator it = keys.iterator();
				while(it.hasNext())
				{
					SelectionKey key = (SelectionKey)it.next();
					if((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT)
					{
						Socket client = sock.accept();
						//System.out.println(client);
						synchronized(clientArray)
						{
							clientArray.add(client);
						}
						SocketChannel sc = client.getChannel();
						ByteBuffer buff = ByteBuffer.allocate(1024);
						String message="Hello there\n";
						byte[] bytebuff=message.getBytes("UTF-8");
						buff.put(bytebuff);
						buff.flip();
						sc.write(buff);
						//System.out.println(sc);
						sc.configureBlocking(false);
						sc.register(selector,SelectionKey.OP_READ);
						System.out.println("New client: "+client.getRemoteSocketAddress().toString());
						System.out.println("Client Array size is "+clientArray);
					}
					if((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ)
					{
						SocketChannel sc=null;
						try
						{

							sc=(SocketChannel)key.channel();
							Socket client=sc.socket();
							ByteBuffer buff = ByteBuffer.allocate(1024);
							int status=sc.read(buff);
							//System.out.println(status);
							if(status==-1)
							{
								synchronized(clientArray)
								{
									clientArray.remove(client);
								}
								sc.close();
								System.out.println("Client array size is "+clientArray.size());
								continue;
							}
							//buff.flip();
							String message=new String(buff.array(),java.nio.charset.StandardCharsets.UTF_8);
							msgQueue.add(message);
							//System.out.println("Queue size is "+msgQueue.size());
							//broadcast(client.getRemoteSocketAddress().toString(),message);

						}
						catch(IOException e)
						{
							e.printStackTrace();
							key.cancel();
							try
							{
								sc.close();
							}
							catch(IOException e2)
							{
								System.err.println("Error closing the socketchannel "+e2);
							}
							System.err.println("Exception occurred");
						}
					}
				}
				keys.clear();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

	}
	public void broadcast()
	{
		System.out.println("Broadcast started");
		while(true)
		{
			//System.out.println("Iterating");
			String message=null;
			if(!msgQueue.isEmpty())
			{
				message=msgQueue.poll();
				//System.out.println("Retrieved message from the queue is "+message);
			}
			if(clientArray.size()!=0)
			{
				synchronized(clientArray)
				{
					for(Socket sock : clientArray)
					{
						try
						{
							// BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
							// bw.write("Hello");
							SocketChannel sc = sock.getChannel();
							ByteBuffer buff = ByteBuffer.allocate(1024);
							//System.out.println(message);
							if(message!=null)
							{
								byte[] byteBuff=message.getBytes("UTF-8");
								buff.clear();
								buff.put(byteBuff);
								buff.flip();
								//System.out.println("Writing");
								sc.write(buff);
								//System.out.println("Wrote");
								//System.out.println(sock);
							}
						}
						catch(IOException e){}
						//sock.send(message);
					}
				}
			}
		}
	}
}