package com.mygubbi.game.dashboard.view.proposals;


import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.AccessoryDetails;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.sql.Date;
/**
 * Created by User on 27-06-2017.
 */
public class ModuleCountReport {


    public static void main(String[] args) {
         ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
        String codeForProductWOTax="";
        String codeForStdManfCost="";
        String codeForNStdManfCost="";
        String codeForManfLabourCost="";
        String codeForAddonWOTax="";
        String codeForAddonSourcePrice="";

        double rateForProductWOTax;
        double rateForStdManfCost;
        double rateForNStdManfCost;
        double rateForManfLabourCost;
        double rateForAddonWOTax;
        double rateForAddonSourcePrice;
        double rateForLconnectorPrice;

        Double TotalCost = 0.0;
        Double NSWoodWorkCost=0.0;
        Double SWoodWorkCost=0.0;
        Double ShutterCost=0.0;
        Double CarcassCost=0.0;
        Double AccessoryCost=0.0;
        Double HardwareCost=0.0;
        Double LabourCost=0.0;
        Double hikeCost=0.0;
        Double handleAndKnonCost=0.0;
        Double hingeCost=0.0;

        Double stdModuleManufacturingCost =0.0;
        Double nonStdModuleManufacturingCost =0.0;
        Double manufacturingLabourCost =0.0;
        Double manufacturingHardwareCost =0.0;
        Double manufacturingAccessoryCost =0.0;
        Double manufacturingHandleAndKnobCost =0.0;
        Double manufacturingHingeCost =0.0;
        Double manufacturingHikeCost =0.0;
        Double FinalmanufacturingAccoryCost=0.0;
        Double manufacturingAccessoryCostForZgeneric=0.0;
        Double manufacturingTotalSalesPrice =0.0;
        Double manufacturingProfit =0.0;
        Double marginCompute=0.0;

        int nonStdModuleCount =0;
        int standardModuleCount =0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mg","root", "root");
            Statement stmt = con.createStatement();

            String sDate1="2017-04-19 00:00:00";
            ProposalHeader proposalHeader = proposalDataProvider.getProposalHeader(519);
            String city=proposalHeader.getPcity();
            Date priceDate=proposalHeader.getPriceDate();
            System.out.println("PriceDate " +priceDate);
            Double Amount=0.0;
                Double lConnectorSourcePrice=0.0;
                List<Product> products;

                List<RateCard> rateCard=proposalDataProvider.getFactorRateCodeDetails("F:PRODWOTAX");
                for (RateCard productwotaxcode : rateCard) {
                    codeForProductWOTax=productwotaxcode.getCode();
                }

                List<RateCard> manfstdcostlist=proposalDataProvider.getFactorRateCodeDetails("F:STDMC");
                for (RateCard manfstdcode :manfstdcostlist ) {
                    codeForStdManfCost=manfstdcode.getCode();
                }

                List<RateCard> manfnstdcostlist=proposalDataProvider.getFactorRateCodeDetails("F:NSTDMC");
                for (RateCard manfnstdcode :manfnstdcostlist ) {
                    codeForNStdManfCost=manfnstdcode.getCode();
                }

                List<RateCard> labourcostlist=proposalDataProvider.getFactorRateCodeDetails("F:LC");
                for (RateCard labourcostcode : labourcostlist ) {
                    codeForManfLabourCost=labourcostcode.getCode();
                }

                List<RateCard> Addonwotaxlist=proposalDataProvider.getFactorRateCodeDetails("F:ADWOTAX");
                for (RateCard addonWOcode : Addonwotaxlist ) {
                    codeForAddonWOTax=addonWOcode.getCode();
                }

                List<RateCard> Addonsourcepricelist=proposalDataProvider.getFactorRateCodeDetails("F:CASP");
                for (RateCard addonsourceprice : Addonsourcepricelist ) {
                    codeForAddonSourcePrice=addonsourceprice.getCode();
                }

                products = proposalDataProvider.getVersionProducts(519,"1.8");
                System.out.println("Products Size " +products.size());
                for(Product product:products)
                {
                    if(Objects.equals(product.getProductCategoryCode(), "Wardrobe"))
                    {
                        PriceMaster productWOtaxpriceMaster=proposalDataProvider.getFactorRatePriceDetails("STDMC:Wardrobe",priceDate,city);
                        rateForStdManfCost=productWOtaxpriceMaster.getSourcePrice();
                        // LOG.info("rateForStdManfCost for wardrobe " +rateForStdManfCost);
                    }
                    else
                    {
                        PriceMaster stdmanfcostpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForStdManfCost,priceDate,city);
                        rateForStdManfCost=stdmanfcostpriceMaster.getSourcePrice();
                        //LOG.info("rateForStdManfCost other than wardrobe " +rateForStdManfCost);
                    }
                    //LOG.info("rate for standard manufacture cost" +rateForStdManfCost);
                    PriceMaster productWOtaxpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForProductWOTax,priceDate,city);
                    rateForProductWOTax=productWOtaxpriceMaster.getSourcePrice();
                    PriceMaster nstdmanfcostpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForNStdManfCost,priceDate,city);
                    rateForNStdManfCost=nstdmanfcostpriceMaster.getSourcePrice();
                    PriceMaster labourcostpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForManfLabourCost,priceDate,city);
                    rateForManfLabourCost=labourcostpriceMaster.getSourcePrice();
                    PriceMaster addonwotaxpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForAddonWOTax,priceDate,city);
                    rateForAddonWOTax=addonwotaxpriceMaster.getSourcePrice();
                    PriceMaster addonsourcepricepriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForAddonSourcePrice,priceDate,city);
                    rateForAddonSourcePrice=addonsourcepricepriceMaster.getSourcePrice();
                    PriceMaster lConnectorRate=proposalDataProvider.getHardwareRateDetails("H074",priceDate,city);
                    rateForLconnectorPrice=lConnectorRate.getSourcePrice();
                    // LOG.info("rateForLconnectorPrice " +rateForLconnectorPrice);
                    Amount+=product.getAmount();
                    /*if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes"))
                    {
                        if(product.getHandleTypeSelection().equals("Gola Profile"))
                        {
                            lConnectorSourcePrice = product.getNoOfLengths() * rateForLconnectorPrice;
                        }
                    }*/
                    manufacturingHandleAndKnobCost+=lConnectorSourcePrice;
                    //LOG.info("lConnectorSourcePrice" +lConnectorSourcePrice);
                    //LOG.info("rateForStdManfCost" +rateForStdManfCost+ "rateForNStdManfCost" +rateForNStdManfCost+ "rateForManfLabourCost" +rateForManfLabourCost);
                    /*List<Module> modules=product.getModules();
                    for(Module module:modules)
                    {
                        ModuleForPrice moduleForPrice = new ModuleForPrice();
                        moduleForPrice.setCity(city);
                        moduleForPrice.setModule(module);
                        moduleForPrice.setProduct(product);
                        if (priceDate == null)
                        {
                            Date dateToBeUsed = new Date(System.currentTimeMillis());
                            moduleForPrice.setPriceDate(dateToBeUsed);
                        }
                        else {
                            moduleForPrice.setPriceDate(priceDate);
                        }
                        ModulePrice modulePrice = proposalDataProvider.getModulePrice(moduleForPrice);
                        if(module.getMgCode().startsWith("MG-NS-H"))
                        {
                            hikeCost+=modulePrice.getWoodworkCost();
                        }
                    }*/

                    int i=1;
                    List<Module> modulesToUse=product.getModules();
                    System.out.println("modules size " +modulesToUse.size());
                    for(Module module:modulesToUse)
                    {
                        Double HardwareSourcePrice=0.0;
                        Double AccessorySourcePrice=0.0;
                        Double standardWC=0.0;
                        Double nonstandardWC=0.0;

                        ModuleForPrice moduleForPrice = new ModuleForPrice();
                        moduleForPrice.setCity(city);
                        moduleForPrice.setModule(module);
                        moduleForPrice.setProduct(product);
                        if (priceDate == null)
                        {
                            Date dateToBeUsed = new Date(System.currentTimeMillis());
                            moduleForPrice.setPriceDate(dateToBeUsed);
                        }
                        else
                        {
                            moduleForPrice.setPriceDate(priceDate);
                        }
                        ModulePrice modulePrice = proposalDataProvider.getModulePrice(moduleForPrice);
                        TotalCost+=modulePrice.getTotalCost();
                        if (module.getMgCode().startsWith("MG-NS"))
                        {
                            nonStdModuleCount++;
                            if(!(module.getMgCode().startsWith("MG-NS-H")))
                            {
                                nonstandardWC+=modulePrice.getCarcassCost()+modulePrice.getShutterCost();
                                NSWoodWorkCost+=modulePrice.getCarcassCost()+modulePrice.getShutterCost();
                            }
                        }
                        else
                        {
                            standardModuleCount++;
                            standardWC=modulePrice.getCarcassCost()+modulePrice.getShutterCost();
                            SWoodWorkCost+=modulePrice.getCarcassCost()+modulePrice.getShutterCost();
                            //LOG.info("SWoodWorkCost" +modulePrice.getCarcassCost()+modulePrice.getShutterCost());
                        }
                        ShutterCost+=modulePrice.getShutterCost();
                        CarcassCost+=modulePrice.getCarcassCost();
                        AccessoryCost += modulePrice.getAccessoryCost();//msp
                        handleAndKnonCost +=modulePrice.getHandleAndKnobCost();
                        // LOG.info(lConnectorSourcePrice);
                        manufacturingHandleAndKnobCost+=modulePrice.getHandleAndKnobSourceCost();
                        hingeCost+=modulePrice.getHingeCost();

                        for(ModuleHingeMap moduleHingeMap:module.getHingePack())
                        {
                            PriceMaster hingedetails=proposalDataProvider.getHingeRateDetails(moduleHingeMap.getHingecode(),priceDate,city);
                            {
                                manufacturingHingeCost+=hingedetails.getSourcePrice() * moduleHingeMap.getQty();
                            }
                        }

                        List<ModuleAccessoryPack> moduleaccpack=module.getAccessoryPacks();
                        for(ModuleAccessoryPack moduleAccessoryPack:moduleaccpack)
                        {
                            List<String> acccode=moduleAccessoryPack.getAccessories();
                            for(String ZgenericAccessory: acccode)
                            {
                                PriceMaster accessoryRateMaster=proposalDataProvider.getAccessoryRateDetails(ZgenericAccessory,priceDate,city);
                                {
                                    AccessorySourcePrice+=accessoryRateMaster.getSourcePrice();
                                    manufacturingAccessoryCost+=accessoryRateMaster.getSourcePrice();
                                    //LOG.info("ACCEssory Z generic" +ZgenericAccessory+ "SP  "+accessoryRateMaster.getSourcePrice());
                                }
                            }

                            List <AccessoryDetails> accesoryNormal=proposalDataProvider.getAccessoryhwDetails(moduleAccessoryPack.getCode());
                            for(AccessoryDetails acc: accesoryNormal)
                            {
                                PriceMaster hardwareRateMaster=proposalDataProvider.getHardwareRateDetails(acc.getCode(),priceDate,city);
                                {
                                    HardwareSourcePrice+=hardwareRateMaster.getSourcePrice()*Double.valueOf(acc.getQty());
                                    manufacturingHardwareCost+=hardwareRateMaster.getSourcePrice()*Double.valueOf(acc.getQty());
                                    //LOG.info("ACC HW Price " +acc.getCode() + " " + hardwareRateMaster.getSourcePrice() + " " +acc.getQty());
                                }
                            }
                            List <AccessoryDetails>  accesoryHardwareMasters =proposalDataProvider.getAccessoryDetails(moduleAccessoryPack.getCode());
                            for(AccessoryDetails acc: accesoryHardwareMasters)
                            {
                                PriceMaster accessoryRateMaster=proposalDataProvider.getAccessoryRateDetails(acc.getCode(),priceDate,city);
                                {
                                    AccessorySourcePrice+=accessoryRateMaster.getSourcePrice();
                                    manufacturingAccessoryCostForZgeneric+=accessoryRateMaster.getSourcePrice();
                                    //LOG.info("ACCEssory " +acc.getCode()+ "SP  "+accessoryRateMaster.getSourcePrice());
                                }
                                PriceMaster hardwareRateMaster=proposalDataProvider.getHardwareRateDetails(acc.getCode(),priceDate,city);
                                {
                                    {
                                        HardwareSourcePrice+=hardwareRateMaster.getSourcePrice();
                                        manufacturingHardwareCost += hardwareRateMaster.getSourcePrice();
                                      //  LOG.info("ACC Z generic " +acc.getCode()+ " " +hardwareRateMaster.getSourcePrice() + " acc qty" +acc.getQty());

                                    }
                                }
                            }
                        }
                        HardwareCost+=modulePrice.getHardwareCost();
                        if(!(module.getMgCode().startsWith("MG-NS-H")))
                        {
                            LabourCost+=modulePrice.getLabourCost();
                        }
                        List<ModuleComponent> modulehardwaredetails=proposalDataProvider.getModuleAccessoryhwDetails(module.getMgCode());
                        for(ModuleComponent acchwdetails: modulehardwaredetails)
                        {
                            PriceMaster hardwareRateMaster=proposalDataProvider.getHardwareRateDetails(acchwdetails.getCompcode(),priceDate,city);
                            {
                                {
                                    double quantity = 0.0;
                                    if (acchwdetails.getQuantityFlag() == null || acchwdetails.getQuantityFlag().equals("") || acchwdetails.getQuantityFlag().equals("F") )
                                    {
                                        quantity = acchwdetails.getQuantity();
                                    }
                                    else if (acchwdetails.getQuantityFlag().equals("C"))
                                    {
                                        quantity = calculateQuantityUsingFormula(acchwdetails.getQuantityFormula(),module);
                                    }
                                    HardwareSourcePrice+=hardwareRateMaster.getSourcePrice() * quantity;
                                    manufacturingHardwareCost += hardwareRateMaster.getSourcePrice() * quantity;
                                    //LOG.info("Hardware data" +acchwdetails.getCompcode()+ " " +hardwareRateMaster.getSourcePrice() +" " + quantity);
                                }
                            }
                        }
                        //int insert = stmt.executeUpdate("insert into margin_report(CarcassPrice,ShutterPrice,WoodWorkPrice,LabourPrice,HardwarePrice,AccessoryPrice,MHardwarePrice,MAccessoryPrice) values ("+ "'" +modulePrice.getCarcassCost() +"'"+ "," + "'" +modulePrice.getShutterCost()+ "," + "'" +modulePrice.getWoodworkCost()+ "," + "'" +modulePrice.getLabourCost()+ "," +"'" +modulePrice.getHardwareCost()+ "," + "'" +modulePrice.getAccessoryCost()+ "," +"'" +HardwareSourcePrice+ "," + "'" +AccessorySourcePrice+ "'" + ")");
                        System.out.println("Execute query next");
                        int insert = stmt.executeUpdate("insert into margin_report(productName,MgCode,CarcassPrice,ShutterPrice,WoodWorkPrice,standardWP,NonStdWP,LabourPrice,HardwarePrice,AccessoryPrice,MHardwarePrice,MAccessoryPrice) values ("+ "'" +product.getProductCategoryCode()+"'" + "," + "'" +module.getMgCode() + "'" + "," + modulePrice.getCarcassCost() + "," +modulePrice.getShutterCost()+ "," +modulePrice.getWoodworkCost()+ "," +standardWC+ "," +nonstandardWC+ "," +modulePrice.getLabourCost()+ "," +modulePrice.getHardwareCost()+ "," +modulePrice.getAccessoryCost()+ "," +HardwareSourcePrice+ "," +AccessorySourcePrice + ")");
                        System.out.println(stmt.toString());
                        //LOG.info("Module Code " +module.getMgCode()+ "Shutter cost " +modulePrice.getShutterCost()+ "Carcass cost " +modulePrice.getCarcassCost()+ "Hardware cost " +modulePrice.getHardwareCost()+ "Labour cost " +modulePrice.getLabourCost()+ "Accessory Cost" +AccessoryCost+ "Handle and knob Cost " +handleAndKnonCost);
                    }
                }
        }
        catch(Exception e) {
            System.out.println("SQL exception occured " + e);
            e.printStackTrace();
        }
    }
    private static double calculateQuantityUsingFormula(String quantityFormula, Module productModule)
    {
        switch (quantityFormula)
        {
            case "F1":
                return (productModule.getWidth() > 1000) ? 4 : 2;
            case "F2":
                return new Double((productModule.getHeight() * productModule.getWidth() * 1.05) / (1000 * 1000));
            case "F3":
                return new Double(productModule.getWidth() * 1.05) / 100;
            case "F4":
                return new Double(productModule.getWidth() * productModule.getDepth() * 1.05) / (1000 * 1000) * 4;
            case "F5":
                return new Double(productModule.getWidth() * productModule.getDepth() * 1.05) / (1000 * 1000) / 2;
            case "F6":
                int value1 = (productModule.getHeight() > 2100) ? 5 : 4;
                int value2 = (productModule.getWidth() > 600) ? 2 : 1;
                return value1 * value2;
            case "F7":
                return new Double(productModule.getHeight() * 2 + productModule.getWidth()) / 1000;
            case "F8":
                return new Double((productModule.getHeight() + productModule.getWidth() * 2) * 1.05) / 1000;
            case "F9":
                return new Double((productModule.getHeight() * 2 + productModule.getWidth()) * 1.05) / 1000;
            case "F10":
                return (productModule.getWidth() > 600) ? 6 : 4;
            case "F11":
                return new Double(productModule.getWidth() * 1.05) / 1000;
            case "F12":
                return (productModule.getWidth() >= 601) ? 4 : 2;
            case "F13":
                return (productModule.getWidth() >= 1001) ? 2 : 1;
            case "F14":
                return new Double(productModule.getWidth() * productModule.getDepth() * 10.764) / (1000 * 1000);
            case "F15":
                return new Double(productModule.getHeight() * 1.05)/1000;
            case "F16":
                return (productModule.getWidth() > 600) ? 8 : 4;
            case "F17":
                return (productModule.getWidth() > 600) ? 3 : 2;
            case "F18":
                int valuenew1 = (productModule.getHeight() > 2100) ? 5 : 4;
                int valuenew2 = (productModule.getWidth() > 600) ? 2 : 1;
                return valuenew1 * valuenew2 * 4;
            case "F19":
                return (productModule.getWidth() > 600) ? 12 : 8;
            case "F20":
                return (productModule.getWidth() > 600) ? 16 : 8;
            case "F21":
                return new Double((productModule.getWidth() * productModule.getDepth() * 1.05) / (1000 * 1000));
            case "F22":
                return new Double(productModule.getHeight() + productModule.getWidth() * 2) / 1000;
            default:
                return 0;
        }
    }
}
