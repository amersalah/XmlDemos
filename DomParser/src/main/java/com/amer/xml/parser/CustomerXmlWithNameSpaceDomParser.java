package com.amer.xml.parser;


import com.amer.xml.data.Address;
import com.amer.xml.data.Customer;
import com.amer.xml.data.ObjectPrinter;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomerXmlWithNameSpaceDomParser {

    private final static String CUSTOMER_NS = "http://www.thoughtbend.com/customer/v1";
    private final static String ADDRESS_NS = "http://www.thoughtbend.com/addr/v1";

    public static void main(String[] args)  {
        try(InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")){

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder  documentBuilder = builderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            NodeList customerNodeList = document.getElementsByTagNameNS( CUSTOMER_NS ,"customer");
            List<Customer> customers = new ArrayList<>();

            for( int i=0 ;i<customerNodeList.getLength() ; i++)
            {
                customers.add(buildCustomerFromNode(customerNodeList.item(i)));
            }

            for (Customer customer : customers)
            {
                ObjectPrinter.printCustomer(customer);
            }


        }catch (IOException | ParserConfigurationException | SAXException ex)
        {
            System.out.println(System.err);
        }
    }

    private static Customer buildCustomerFromNode(Node node)
    {
        Customer  customer = new Customer();

        NamedNodeMap attributes = node.getAttributes();
        Attr attr = (Attr) attributes.getNamedItem("id");
        String id = attr.getValue();
        customer.setId(Long.parseLong(id));
        boolean noMatch =false;

        NodeList childNodes = node.getChildNodes();
        
        for(int i= 0; i<childNodes.getLength() ; i++)
        {
            Node dataNode = childNodes.item(i);

            if(dataNode instanceof Element)
            {
                Element dataElement = (Element) dataNode;
                switch (dataElement.getLocalName()){
                    case ("firstName"):
                        customer.setFirstName(dataElement.getTextContent());
                        break;
                    case("lastName"):
                        customer.setLastName(dataElement.getTextContent());
                        break;
                    case("email"):
                        customer.setEmailAddress(dataElement.getTextContent());
                        break;
                    default:
                        noMatch= true;
                        break;
                }

                if(noMatch)
                {
                    if(ADDRESS_NS.equals(dataElement.getNamespaceURI()) && "addresses".equals(dataElement.getLocalName()))
                    {
                        customer.setAddresses(new ArrayList<>());

                        NodeList addressNodeList = dataElement.getChildNodes();

                        for(int j= 0; j<addressNodeList.getLength() ; j++)
                        {
                            Node addressNode = addressNodeList.item(j);

                            if(addressNode instanceof  Element)
                            {
                                Element addressElement = (Element) addressNode;
                                if(ADDRESS_NS.equals(dataElement.getNamespaceURI()) && "address".equals(addressElement.getLocalName()))
                                {
                                    customer.getAddresses().add(buildAddressFromNode(addressElement));
                                }
                            }
                        }

                    }
                }
            }
        }


        return customer;
    }

    private static Address buildAddressFromNode(Node nodeAddress)
    {
       Address address = new Address();

        NodeList childNodes = nodeAddress.getChildNodes();

        for(int i= 0; i<childNodes.getLength() ;i++)
        {
            Node addressNode = childNodes.item(i);

            if(addressNode instanceof Element)
            {
                Element dataElement = (Element)addressNode;

                switch (dataElement.getLocalName())
                {
                    case ("type"):
                        address.setAddressType(dataElement.getTextContent());
                        break;
                    case("street"):
                        address.setStreet1(dataElement.getTextContent());
                        break;
                    case ("city"):
                        address.setCity(dataElement.getTextContent());
                        break;
                    case("state"):
                        address.setState(dataElement.getTextContent());
                        break;
                    case("zip"):
                        address.setZip(dataElement.getTextContent());
                        break;
                }
            }
        }

        return address;
    }
}
