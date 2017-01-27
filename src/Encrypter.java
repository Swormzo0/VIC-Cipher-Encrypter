import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class Encrypter extends JPanel implements ActionListener 
{
	private static final long serialVersionUID = 1L;
	protected JTextArea input, output;
	protected JTextField key;
	protected JButton enter, randomize;
	protected JDialog fileDialog;
	static final String encryptString = "Encrypt";
	static final String decryptString = "Decrypt";
	static String statKey, inputText, outputText, keyText, cipher, plain="";
	static final String validhash= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ .";
	static final String validkey1="aeinot. 1234567890";
	static final String validkey2="bcdfghjklmpqrsuvwxyz";
	static String[] statKeyArray, keyStringArray;
	static Boolean option =true;
	static Boolean randBool =true;
	static int inputLengthVar, outputLengthVar, keyLengthVar;
	static char staticR1, staticR2;
	static char[] keyhash, opKeyCha=new char[]{' '};
	
	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable(){public void run(){createAndShowGUI();}});	
	}
	
	private static void createAndShowGUI()

	{
		JFrame frame = new JFrame("VIC Cipher Encrypter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new Encrypter());
		frame.pack();
		frame.setLocationRelativeTo(frame);
		frame.setVisible(true);
	}

	public Encrypter() 
	{
		//Prepare layout
		super(new GridBagLayout());
		//Prepare Instructions
		JLabel instructions = new JLabel("For the plaintext, only use the punctuation \".\"");
		//Prepare encrypt option
		JRadioButton encryptOption = new JRadioButton(encryptString);
		encryptOption.setMnemonic(KeyEvent.VK_E);
		encryptOption.setActionCommand(encryptString);
		encryptOption.setSelected(true);
		//Prepare decrypt option
		JRadioButton decryptOption = new JRadioButton(decryptString);
		decryptOption.setMnemonic(KeyEvent.VK_D);
		decryptOption.setActionCommand(decryptString);
		//Group the two options together
		ButtonGroup g = new ButtonGroup();
		g.add(encryptOption);
		g.add(decryptOption);
		//Add the action listeners
		encryptOption.addActionListener(this);
		decryptOption.addActionListener(this);
		//Put the options into their own panels
		JPanel panel= new JPanel(new GridLayout(1,0)); JPanel panel2 = new JPanel(new GridLayout(1,0));
		panel.add(encryptOption);
		panel2.add(decryptOption);
		//Initialize the plaintext label and the text box
		JLabel plaintext= new JLabel("Plaintext");
		input= new JTextArea(15,20);
		input.setToolTipText("Put the plain, unencrypted text here!");
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
		input.getDocument().addDocumentListener(new myDocumentListener());
		JScrollPane plainPane = new JScrollPane(input);
		//Initialize the key label and the text box
		JLabel keyLabel = new JLabel("Secret Key");
		key = new JTextField(20);
		key.setToolTipText("Put the secret key here!");
		key.setEditable(false);
		key.setText(keyGen(new String("")));
		//Initialize the ciphertext label and the text box
		JLabel ciphertext = new JLabel("Ciphertext");
		output= new JTextArea(15,20);
		output.setToolTipText("Put the encrypted jumble of text here!");
		output.setLineWrap(true);
		output.setEditable(false);
		JScrollPane plainPane2 = new JScrollPane(output);
		//Initialize the buttons
		enter = new JButton("Enter");
		enter.setMnemonic(KeyEvent.VK_N);
		enter.setToolTipText("Press to en/decrypt!");
		enter.setActionCommand("Enter");
		enter.addActionListener(this);
		
		randomize = new JButton("Randomize");
		randomize.setMnemonic(KeyEvent.VK_R);
		randomize.setToolTipText("Press to randomize the key!");
		randomize.setActionCommand("Randomize");
		randomize.addActionListener(this);
		//Add the components and format where they are placed, etc.
		GridBagConstraints a = new GridBagConstraints();
		a.gridx=1;
		a.weightx=0.5;
		a.weighty=0.5;
		a.gridwidth=2;
		a.insets = new Insets(10,2,2,2);
		a.anchor = GridBagConstraints.PAGE_START;
		add(instructions,a);
		
		a.anchor = GridBagConstraints.LINE_START;
		a.gridwidth=1;
		a.fill=GridBagConstraints.HORIZONTAL;
		a.insets = new Insets(2,10,2,5);
		a.gridy=1;
		add(panel,a);
		
		a.gridx=2;
		add(keyLabel,a);
		
		a.fill=GridBagConstraints.NONE;
		a.anchor = GridBagConstraints.CENTER;
		add(randomize,a);
		
		a.fill=GridBagConstraints.HORIZONTAL;
		a.anchor = GridBagConstraints.LINE_END;
		a.gridwidth=1;
		a.gridx=1;
		a.gridy=2;
		add(panel2,a);
		
		a.gridx=2;
		a.insets=new Insets(3,10,10,10);
		add(key,a);
		
		a.insets = new Insets(3,10,2,10);
		a.gridwidth = GridBagConstraints.RELATIVE;
		a.gridx=1;
		a.gridwidth=1;
		a.gridy=3;
		add(plaintext,a);
		
		a.gridx=2;
		a.gridwidth = GridBagConstraints.REMAINDER;
		add(ciphertext,a);
		
		a.anchor=GridBagConstraints.CENTER;
		a.fill=GridBagConstraints.BOTH;
		a.gridy=4;
		a.gridx=0;
		a.gridwidth = GridBagConstraints.RELATIVE;
		a.gridheight=2;
		add(plainPane,a);
		
		a.gridwidth=1;
		a.gridx=2;
		a.gridwidth = GridBagConstraints.REMAINDER;
		add(plainPane2,a);
		
		a.insets = new Insets(3,10,10,10);
		a.fill=GridBagConstraints.HORIZONTAL;
		a.gridx=1;
		a.gridwidth=2;
		a.gridheight=1;
		a.gridy=6;
		add(enter,a);
	}

	public void actionPerformed(ActionEvent e) 
	{
		if ("Enter".equals(e.getActionCommand()))	
		{
			if (option)
			{
				//Setting up time log
				long encT0=System.currentTimeMillis();
				inputText= input.getText();
				inputLengthVar=inputText.length();
				//Center converted material
				output.setText("");
				try
				{String stringCiphertext=Encryption(new String(""));
				output.setText(stringCiphertext);}
				catch(OutOfMemoryError ee)
				{
					output.setText("java.lang.OutOfMemoryError: Java heap space");
				}
				//Recording Time log
				long encT1=System.currentTimeMillis();
				System.out.print("Encryption: ");
				System.out.print(encT1-encT0);
				System.out.println(" milliseconds.");
			}
			else
			{
				outputText= output.getText().toLowerCase().replaceAll("[^0-9]", "");
				outputLengthVar=outputText.length();
				keyText= key.getText().toLowerCase();
				keyLengthVar=keyText.length();
				long kRcT0=System.currentTimeMillis();
				boolean keyBool=keyRec(false);
				long kRcT1=System.currentTimeMillis();
				System.out.print("Keyrec: "); System.out.print(kRcT1-kRcT0); System.out.println(" milliseconds.");
				long decT0=System.currentTimeMillis();
				if (keyBool==true)
				{
					String stringPlainText=Decryption(new String(""));
					input.setText(stringPlainText);
				}
				else
				{
					input.setText("Invalid key or output entered");
				}
				//Recording Time log
				long decT1=System.currentTimeMillis();
				System.out.print("Decryption: ");
				System.out.print(decT1-decT0);
				System.out.println(" milliseconds.");
			}
		}
		else if ("Decrypt".equals(e.getActionCommand())) 
		{
			if (option)
			{
				input.setText("");
				output.setText("");
			}
			key.setText("");
			key.setEditable(true);
			option = false;
			output.setEditable(true);
			output.setWrapStyleWord(true);
			input.setEditable(false);
			input.setWrapStyleWord(false);
		}
		else if ("Encrypt".equals(e.getActionCommand())) 
		{
			if (option==false)
			{
				input.setText("");
				output.setText("");
			}
			option = true;
			key.setText(statKey);
			key.setEditable(false);
			output.setEditable(false);
			output.setWrapStyleWord(false);
			input.setEditable(true);
			input.setWrapStyleWord(true);
		}
		else if ("Randomize".equals(e.getActionCommand())) 
		{
			key.setText(keyGen(new String("")));			
		}
	}
	
	public static String keyGen(String keyText1)
	{
		//Setting up the timer
		long keyT0=System.currentTimeMillis();
		//Prepare the permutation of the letters and punctuation
		//Charhash=selection of stuff to choose from, keyhash is the array that becomes the key
		/*New plan: two charhashes, one with Estonia-R + 0,0 , the other with everything else
		Get indexOf the two placeholders, set indexOf to r1,r2, replace them with the values
		Then replace the remainder with the second charhash*/
		char[] charhashCom= new char[]{'a','e','i','n','o','t','.',' ','!','!'};
		char[] charhash= new char[]{'b','c','d','f','g','h','j','k','l','m','p','q','r','s','u','v','w','x','y','z',};
		keyhash= new char[30];
		//Generates a permutation of 30 chars from charhashCom&charhash into keyhash
		Random g = new Random();
		for (int i=0; i<10; i++)
		{
			int r = g.nextInt(10);
			while (charhashCom[r]=='0'||charhashCom[r]=='!'&&i==0)
			{
				r = g.nextInt(10);
			}
			keyhash[i]=charhashCom[r];
			charhashCom[r]='0';
		}
		for (int i=0; i<20; i++)
		{
			int r = g.nextInt(20);
			while (charhash[r]=='0')
			{
				r = g.nextInt(20);
			}
			keyhash[i+10]=charhash[r];
			charhash[r]='0';
		}
		//Swaps two numbers with two random chars
		StringBuilder s = new StringBuilder();
		s.append(keyhash);
		int r1 = s.toString().indexOf('!');
		char charR1= Character.forDigit(r1, 10);
		keyhash[r1]=charR1;
		int r2 = s.toString().lastIndexOf('!');
		char charR2= Character.forDigit(r2, 10);
		keyhash[r2]=charR2;
		s = new StringBuilder().append(keyhash);
		staticR1=charR1;
		staticR2=charR2;
		statKey=s.toString();
		//Make the number key in a String[] format
		keyStringArray=new String[30];
		for (int i=0;i<30;i++)
		{
			keyStringArray[i]=Integer.toString(i);
		}
		for (int i=10;i<20;i++)
		{
			keyStringArray[i]=staticR1+keyStringArray[i-10];
		}
		for (int i=20;i<30;i++)
		{
			keyStringArray[i]=staticR2+keyStringArray[i-20];
		}
		//Make the hash key into a String[]
		statKeyArray= new String[30];
		for (int i=0;i<30;i++)
		{
			statKeyArray[i]=statKey.substring(i,i+1);
		}
		randBool=true;
		//Logging time
		long keyT1=System.currentTimeMillis();
		System.out.print("Key generation: ");
		System.out.print(keyT1-keyT0);
		System.out.println(" milliseconds.");
		return keyText1=statKey;
	}

	public static String Encryption(String ciphertextE)
	{
		//Initializing Variables
		inputText=inputText.replaceAll("[^a-zA-Z. ]", "");
		inputText=inputText.toLowerCase();
		inputLengthVar=inputText.length();
		String[] inputTextStringArray=new String[inputLengthVar]; //Make a new string array with the length of the input
		for (int i=0;i<inputLengthVar;i++)
		{
			inputTextStringArray[i]=inputText.substring(i,i+1); //Turn the new string array into a one character string array from the input
		}
		char[] inputTextChar=inputText.toCharArray(); //Turn text from plaintext box into char array
		for (int i=0;i<inputLengthVar;i++)
		{
			inputTextStringArray[i]=keyStringArray[statKey.indexOf(inputTextChar[i])]; //Otherwise, it replaces the character with the corresponding number key
		}
		String[] convertedStringArray=new String[]{""}; //Turn inputTextStringArray from a string array into a string (kinda!) :p
		
		convertedStringArray[0]=Arrays.toString(inputTextStringArray).replaceAll(", |\\u005B|\\u005D", "");
		if(convertedStringArray[0].isEmpty()) //Fixes weird glitch
		{
			convertedStringArray[0]="";
		}
		
		return ciphertextE=convertedStringArray[0];
	}
	
	public static boolean keyRec(boolean valid)
	{
		if (keyLengthVar!=30)
		{ return valid=false;}
		else 
		{
			boolean validChars=true;
			for(int i=0;i<10;i++)
			{
				if(validkey1.indexOf(keyText.substring(i,i+1))==-1)
				{
					validChars=false;
					break;
				}
			}
			for(int i=10;i<30;i++)
			{
				if(validkey2.indexOf(keyText.substring(i,i+1))==-1)
				{
					validChars=false;
					break;
				}
			}
			if(!outputText.toLowerCase().replaceAll("[^0-9]", "").equals(outputText))
			{
				validChars=false;
			}
			int j=0;
			String[] reckeyNums=new String[2];
			for(int i=0; i<10; i++)
			{
				if (Integer.toString(i).equals(keyText.substring(i,i+1)))
				{
					reckeyNums[j]=Integer.toString(i);
					j++;
				}
			}
			if(outputText.endsWith(reckeyNums[0])||outputText.endsWith(reckeyNums[1])) { validChars=false; }
			if (!validChars)
			{
				{ return valid=false;}
			}
			else
			{
				
					String tester=keyText.substring(0,10).replaceAll("[0-9]","");
					String permu="aienot. ";
					
					boolean isPermu=true;
					int[] occurrences= new int[permu.length()];
					if(tester.length()==permu.length())
					{for(int i=0; i<permu.length();i++)
					{
						if(tester.indexOf(permu.charAt(i))!=-1)
						occurrences[tester.indexOf(permu.charAt(i))]++;
						else {isPermu=false; break;}
					}
					for(int i=0; i<permu.length();i++)
					{
						if(occurrences[i]!=1) {isPermu=false; break;}
						else { }
					}}
					else isPermu=false;
					int[] occurrences2= new int[validkey2.length()];
					String tester2=keyText.substring(10,30).replaceAll("[0-9]","");
					if(tester2.length()==validkey2.length())
					{for(int i=0; i<validkey2.length();i++)
					{
						if(tester2.indexOf(validkey2.charAt(i))!=-1)
						occurrences2[tester2.indexOf(validkey2.charAt(i))]++;
						else {isPermu=false; break;}
					}
					for(int i=0; i<validkey2.length();i++)
					{
						if(occurrences2[i]!=1) {isPermu=false; break;}
						else { }
					}}
					else isPermu=false;
					if(!isPermu)
					{
						return valid=false;
					}
					else
					{
						String key=keyText.substring(0,10);
						int numCount=0;
						for (int i=0; i<key.length();i++)
						{
							if (Integer.toString(i).equals(key.substring(i,i+1)))
							{
								numCount++;
							}
						}
						if(numCount!=2)
						{
							return valid=false;
						}
						else {
							String[] prestatKeyArray=keyText.split("");
							for (int i=0;i<keyStringArray.length;i++)
							{
								statKeyArray[i]=prestatKeyArray[i+1];
							}
							for (int i=10;i<20;i++)
							{
								keyStringArray[i]=reckeyNums[0]+keyStringArray[i-10];
							}
							for (int i=20;i<30;i++)
							{
								keyStringArray[i]=reckeyNums[1]+keyStringArray[i-20];
							}
							return valid=true;}}}
					}
				
	}
	
	public static String Decryption(String plaintextE)
	{
		//Initialising Variables
		keyLengthVar=keyText.length();
		if(outputText.isEmpty()) {outputText="";} //Fixes the weird glitch. :D	
		//char[] outputCha=outputText.toCharArray(); //Turn the output into a char array
		//Determines the two numbers in key.
		int j=0;
		int[] keyNums=new int[2];
		for(int i=0; i<10; i++)
		{
			if (Integer.toString(i).equals(keyText.substring(i,i+1)))
			{
				keyNums[j]=i;
				j++;
			}
		}
		//De-vics
		String[] toPlainText=outputText.split("");
		String keyNums0=Integer.toString(keyNums[0],10);
		String keyNums1=Integer.toString(keyNums[1],10);
		try{
		for(int i=1;i<toPlainText.length;i++)
		{
			if(toPlainText[i].equals(keyNums0))
			{
				toPlainText[i]=statKeyArray[Integer.parseInt(toPlainText[i+1])+10];
				toPlainText[i+1]="";
				i++;
			}
			else if(toPlainText[i].equals(keyNums1))
			{
				toPlainText[i]=statKeyArray[Integer.parseInt(toPlainText[i+1])+20];
				toPlainText[i+1]="";
				i++;
			}
			else toPlainText[i]=statKeyArray[Integer.parseInt(toPlainText[i])];
		}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return plaintextE="Invalid ciphertext";
		}
		String plainText=Arrays.toString(toPlainText).replaceAll(", |\\u005B|\\u005D", "");
		return plaintextE=plainText;
	}
}

class myDocumentListener implements DocumentListener
{
	public void insertUpdate(DocumentEvent e)
	{
		Encrypter.randBool=true;
	}
	public void removeUpdate(DocumentEvent e) {
		Encrypter.randBool=true;
    }
	public void changedUpdate(DocumentEvent e) 
	{
		 //Debugging
	}
}
