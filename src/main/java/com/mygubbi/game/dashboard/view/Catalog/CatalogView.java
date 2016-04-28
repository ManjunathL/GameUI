package com.mygubbi.game.dashboard.view.Catalog;
import com.mygubbi.game.dashboard.DashboardNavigator;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.view.DashboardViewType;
import com.mygubbi.game.dashboard.view.proposals.ProposalsView;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.MouseEvents;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.mygubbi.game.dashboard.domain.JsonPojo.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
/**
 * Created by test on 23-03-2016.
 */
public class CatalogView extends UploadContentComposite implements View, Receiver, SucceededListener, FailedListener {

    public static final String EDIT_ID = "proposals-edit";
    public static final String TITLE_ID = "proposals-title";
    private Label titleLabel;

    private final VerticalLayout root ;
    Navigator navigator;
    public static final String MAINVIEW = "main";
    private MenuBar menuBar = new MenuBar();
    File file;
    String FILE_PATH;
    public CatalogView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        DashboardEventBus.register(this);


/*MenuBar menubar = new MenuBar();
MenuBar.MenuItem menuItem = menubar.addItem("Products List", new MenuBar.Command() {
    @Override
    public void menuSelected(MenuBar.MenuItem selectedItem) {
        UI.getCurrent().getNavigator().navigateTo(MAINVIEW, new ProductsView());
    }
})*/

        root = new VerticalLayout();
        getVerticalLayout_2().addComponent(menuBar);

        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("proposals-view");
/*		setContent(root);
*/		Responsive.makeResponsive(root);
  /*      root.addComponent(Menu());*/
        root.addComponent(buildHeader());

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutEvents.LayoutClickEvent event) {
                DashboardEventBus
                        .post(new DashboardEvent.CloseOpenWindowsEvent());
            }
        });
    }

    private void Upload() {
        Products products = new Products();

        List<Products> productsList = new ArrayList<Products>();

        HSSFWorkbook wb = null;
        Map hm = null;
        Map hm1 = null;
        Map hm2 = null;
        Map hm3 = null;
        try {

/*			final String FILE_PATH = "F://input.xls";
*/
            FileInputStream file = new FileInputStream(new File(FILE_PATH));
            wb = new HSSFWorkbook(file);
            hm = getMfs(wb);
            hm1 = getImgs(wb);
            hm2 = getComps(wb);
            hm3 = getAccs(wb);

            System.out.println("workbook: " + wb);
            HSSFSheet sheet = wb.getSheetAt(0);
            System.out.println("worksheet: " + sheet);
            HSSFRow row;
            // HSSFCell cell;
            // Integer noOfEntries = 1;

            // Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            int dummy = 0;
            while (iterator.hasNext()) {

                products = new Products();
                Row nextRow = iterator.next();
                if (nextRow.getRowNum() == 0
                        || (nextRow == null)){
                    continue; // just skip the rows if row number is 0
                }
                Iterator<Cell> cellIterator = nextRow.cellIterator();
//	    			Iterator rows = sheet.rowIterator();

                Cell cell = cellIterator.next();
//	                    row=(HSSFRow) iterator.next();
                Iterator cells = nextRow.cellIterator();

                cell=(HSSFCell) cells.next();

                if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
                {
                    System.out.print(cell.getStringCellValue()+" ");
                }
                else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                {
                    System.out.print(cell.getNumericCellValue()+" ");
                }
                else if(HSSFDateUtil.isCellDateFormatted(cell)){
                    Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                } else
                {
                    //U Can Handel Boolean, Formula, Errors
                }

                String id_prod=new DataFormatter().formatCellValue(nextRow.getCell(0));
                if(id_prod == null || id_prod.length() < 1)
                    continue;

                products.setId(new DataFormatter().formatCellValue(nextRow.getCell(1)));
                products.setProductId(new DataFormatter().formatCellValue(nextRow.getCell(1)));
                products.setName(new DataFormatter().formatCellValue(nextRow.getCell(2)));
                products.setDesc(new DataFormatter().formatCellValue(nextRow.getCell(3)));
                products.setDimension(new DataFormatter().formatCellValue(nextRow.getCell(4)));
                products.setCategory(new DataFormatter().formatCellValue(nextRow.getCell(5)));
                products.setSubcategory((new DataFormatter().formatCellValue(nextRow.getCell(6))));
                products.setCategoryId(new DataFormatter().formatCellValue(nextRow.getCell(7)));
                products.setSubcategoryId((new DataFormatter().formatCellValue(nextRow.getCell(8))));
                products.setTags((new DataFormatter().formatCellValue(nextRow.getCell(9))));
                products.setDesigner((new DataFormatter().formatCellValue(nextRow.getCell(10))));
                products.setCurr((new DataFormatter().formatCellValue(nextRow.getCell(11))));
                products.setPopularity((new DataFormatter().formatCellValue(nextRow.getCell(12))));
                products.setRelevance((new DataFormatter().formatCellValue(nextRow.getCell(13))));
                products.setShortlisted((new DataFormatter().formatCellValue(nextRow.getCell(14))));
                products.setLikes((new DataFormatter().formatCellValue(nextRow.getCell(15))));
                products.setCreateDt((new DataFormatter().formatCellValue(nextRow.getCell(16))));
                products.setPageId((new DataFormatter().formatCellValue(nextRow.getCell(17))));
                products.setStyleName((new DataFormatter().formatCellValue(nextRow.getCell(18))));
                products.setStyleId((new DataFormatter().formatCellValue(nextRow.getCell(19))));
                products.setPriceRange((new DataFormatter().formatCellValue(nextRow.getCell(20))));
                products.setPriceId((new DataFormatter().formatCellValue(nextRow.getCell(21))));
                products.setDefaultPrice((new DataFormatter().formatCellValue(nextRow.getCell(22))));
                products.setDefaultMaterial((new DataFormatter().formatCellValue(nextRow.getCell(23))));
                products.setDefaultFinish((new DataFormatter().formatCellValue(nextRow.getCell(24))));

                // MF get and set
                ArrayList<Mf> al = (ArrayList<Mf>) hm.get(id_prod);
                Mf[] mfar = new Mf[al.size()];
                System.out.println("mfar:"+al.size()+": "+al);

                //Images Get and Set
	    				/*	ArrayList<Images> alimg = (ArrayList<Images>) hm1.get(id_prod);
	    					Images[] imgfar = new Images[alimg.size()];
	    					System.out.println("imgfar:" + alimg.size() + ": " + alimg);
	    					products.setImages(alimg.toArray(imgfar));*/
                HSSFSheet sheetImg = wb.getSheetAt(2);

                Iterator<Row> iteratorImg = sheetImg.iterator();
                // String id_prod = null;
                List<String> img = new ArrayList<String>();
                while (iteratorImg.hasNext()) {
                    cellIterator = nextRow.cellIterator();
                    Row rowImg = iteratorImg.next();
                    String sno = new DataFormatter().formatCellValue(rowImg
                            .getCell(0));
                    if (sno != null && sno.equals(id_prod)) {
                        String images = new DataFormatter()
                                .formatCellValue(rowImg.getCell(1));
                        img.add(images);
                    } else
                        continue;
                }
                products.setImages(img);
                //Components Get and set
                ArrayList<Components> alcomp= (ArrayList<Components>) hm2.get(id_prod);
                if(alcomp != null && alcomp.size() > 0){
                    Components[] compfar = new Components[alcomp.size()];
                    System.out.println("compfar:" + alcomp.size() + ": " + alcomp);
                    products.setComponents(alcomp.toArray(compfar));
                } else {
                    Components[] compfar = new Components[0];
                    System.out.println("compfar null.");
                    products.setComponents(compfar);
                }

                //Accessories Get and set
                ArrayList<Accessories> alaccs= (ArrayList<Accessories>) hm3.get(id_prod);
                if(alaccs != null && alaccs.size() > 0){
                    Accessories[] compfar = new Accessories[alaccs.size()];
                    System.out.println("compfar:" + alaccs.size() + ": " + alaccs);
                    products.setAccessories(alaccs.toArray(compfar));
                } else {
                    Accessories[] accsfar = new Accessories[0];
                    System.out.println("compfar null.");
                    products.setAccessories(accsfar);
                }
	    					/*
	    					msg.add("hello jackson 2");
	    					msg.add("hello jackson 3");
*/
                           /*HSSFSheet sheetMf = wb.getSheetAt(3);

	    					Iterator<Row> iteratorMf = sheetMf.iterator();
	    					// String id_prod = null;
	    					List<String> msg = new ArrayList<String>();
							while (iteratorMf.hasNext()) {
								cellIterator = nextRow.cellIterator();
								Row rowMf = iteratorMf.next();
								String sno = new DataFormatter().formatCellValue(rowMf.getCell(0));
								System.out.println(sno + " -- ");
								if (sno != null && sno.equals(id_prod)) {
									String messages = new DataFormatter().formatCellValue(rowMf.getCell(1));
									msg.add(messages);
								} else
									continue;
							}
							products.setMessages(msg);*/
                products.setMf(al.toArray(mfar));

                productsList.add(products);
                System.out.println(productsList);



                // JSON CONVERTER
                ObjectMapper mapper = new ObjectMapper();

                System.out.println("productsList: " + products);
                DateFormat dateFormat = new SimpleDateFormat(
                        "yyyy_MM_dd_HH_mm_ss");
                Date date = new Date();
                String createdTime = dateFormat.format(date);
                System.out.println("productsList final: " + products);

                // Convert object to JSON string and save into file directly
                // mapper.writeValue(new File("D:\\"+location+"mygubbi.json"),
                // productsList);
                // Convert object to JSON string and save into file directly
/*				mapper.writeValue(new File("D:\\products.json"), productsList);
*/
                mapper.writeValue(new File(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() +"/JsonFile/" + "product.json"), productsList);

                // Convert object to JSON string
                String jsonInString = mapper.writeValueAsString(productsList);
                System.out.println("JsonInString " + jsonInString);

                // Convert object to JSON string and pretty print
                jsonInString = mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(products);
                System.out.println("Final Json"
                        + mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(products));
                // mapper.writeValue(new
                // File("D:\\"+location+"productsJson.json"), jsonInString);
				/*
				 * File file1 = File.createTempFile(jsonInString, ".tmp");
				 * return new FileOutputStream(file1);
				 */
            }

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // file.getInputStream().close();
            // inputStream.close();

        }
		/* return null; */
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        titleLabel = new Label("Catalog");
        titleLabel.setId(TITLE_ID);
        // titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        return header;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        final String MAINVIEW = "main";

        getUpload_1().setButtonCaption("submit");

        getUpload_1().addListener((Upload.SucceededListener)this);
        getUpload_1().addListener((Upload.FailedListener)this);
        getUpload_1().setReceiver((CatalogView)this);

        /*getButton_1().setCaption("Sample");

        getButton_1().addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {

                getButton_1().setCaption("You made me click!");
            }
        });*/
      /*  btn.addClickListener(new Button.ClickListener() {
            public void buttonClick(MouseEvents.ClickEvent event) {
                btn.setCaption("You made me click!");
            }
        });
        root.addComponent(btn);*/
        /*MenuBar.MenuItem json = menuBar.addItem("JSON",new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
*//*
                getUI().getCurrent().getNavigator().navigateTo(DashboardViewType.PROPOSALS.getViewName());
*//*
            }
        });
        MenuBar.MenuItem json1 = menuBar.addItem("JSON1", new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                *//*navigator = new Navigator(getUI().getCurrent(),getVerticalLayout_2());
                navigator.addView("proposals",ProposalsView.class);
                navigator.navigateTo("proposals");*//*

*//*
                getUI().getCurrent().getNavigator().navigateTo(DashboardViewType.PRODUCTS.getViewName());
*//*

            }
        });*/
    }

    public static Map getMfs(HSSFWorkbook wb) {
        Map hm = new HashMap<String, ArrayList<Mf>>();
        // Fetching the mfs from another sheet.
        HSSFSheet sheetMf = wb.getSheetAt(1);
        Iterator<Row> iteratorMf = sheetMf.iterator();
        ArrayList<Mf> al = new ArrayList<Mf>();
        // String id_prod = null;
        while (iteratorMf.hasNext()) {
            Row rowMf = iteratorMf.next();
            // Iterator<Cell> cellItr1 = rowMf.cellIterator();
            // Cell cell1 = cellItr1.next();
            // Iterator cells1 = rowMf.cellIterator();
            // cell = (HSSFCell) cells1.next();
            String sno = new DataFormatter().formatCellValue(rowMf.getCell(0));
            System.out.println(sno + " -- ");

            if (hm.get(sno) == null) {
                hm.put(sno, new ArrayList());
            }

            // if(sno != null && sno.equals(id_prod)) {
            System.out.println("Got row: ");
            String basePrice = new DataFormatter().formatCellValue(rowMf
                    .getCell(1));
            String material = new DataFormatter().formatCellValue(rowMf
                    .getCell(2));
            String finish = new DataFormatter().formatCellValue(rowMf
                    .getCell(3));
            Mf mf1 = new Mf(basePrice, material, finish);
            System.out.println(mf1);
            // mfar[count] = mf1;
            // count++;
            ArrayList r = (ArrayList) hm.get(sno);
            r.add(mf1);
			/*
			 * } else { System.out.println("breaking.."); break; }
			 */
			/*
			 * if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			 *
			 * }
			 */
        }// End of while mf.
        return hm;
    }

    public static Map getImgs(HSSFWorkbook wb) {
        Map hm1 = new HashMap<String, ArrayList<Images>>();
        // Fetching the Imgs from another sheet.
        HSSFSheet sheetMf = wb.getSheetAt(2);
        Iterator<Row> iteratorMf = sheetMf.iterator();
        ArrayList<Images> al = new ArrayList<Images>();
        // String id_prod = null;
        while (iteratorMf.hasNext()) {
            Row rowMf = iteratorMf.next();
            // Iterator<Cell> cellItr1 = rowMf.cellIterator();
            // Cell cell1 = cellItr1.next();
            // Iterator cells1 = rowMf.cellIterator();
            // cell = (HSSFCell) cells1.next();
            String sno = new DataFormatter().formatCellValue(rowMf.getCell(0));
            System.out.println(sno + " -- ");

            if (hm1.get(sno) == null) {
                hm1.put(sno, new ArrayList());
            }

            // if(sno != null && sno.equals(id_prod)) {
            System.out.println("Got row: ");
            String images = new DataFormatter().formatCellValue(rowMf
                    .getCell(1));

            Images img1 = new Images(images);
            System.out.println(img1);
            // mfar[count] = mf1;
            // count++;
            ArrayList r = (ArrayList) hm1.get(sno);
            r.add(img1);
			/*
			 * } else { System.out.println("breaking.."); break; }
			 */
			/*
			 * if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			 *
			 * }
			 */
        }// End of while mf.
        return hm1;
    }

    public static Map getComps(HSSFWorkbook wb) {
        Map hm2 = new HashMap<String, ArrayList<Components>>();
        // Fetching the mfs from another sheet.
        HSSFSheet sheetMf = wb.getSheetAt(3);
        Iterator<Row> iteratorMf = sheetMf.iterator();
        ArrayList<Components> al = new ArrayList<Components>();
        // String id_prod = null;
        while (iteratorMf.hasNext()) {
            Row rowMf = iteratorMf.next();
            // Iterator<Cell> cellItr1 = rowMf.cellIterator();
            // Cell cell1 = cellItr1.next();
            // Iterator cells1 = rowMf.cellIterator();
            // cell = (HSSFCell) cells1.next();
            String sno = new DataFormatter().formatCellValue(rowMf.getCell(0));
            System.out.println(sno + " -- ");

            if (hm2.get(sno) == null) {
                hm2.put(sno, new ArrayList());
            }

            // if(sno != null && sno.equals(id_prod)) {
            System.out.println("Got row: ");
            String name = new DataFormatter().formatCellValue(rowMf.getCell(1));
            String size = new DataFormatter().formatCellValue(rowMf.getCell(2));
            String qty = new DataFormatter().formatCellValue(rowMf.getCell(3));
            Components comp1 = new Components(name, size, qty);
            System.out.println(comp1);
            // mfar[count] = mf1;
            // count++;
            ArrayList r = (ArrayList) hm2.get(sno);
            r.add(comp1);

        }// End of while comp
        return hm2;
    }

    public static Map getAccs(HSSFWorkbook wb) {
        Map hm3 = new HashMap<String, ArrayList<Accessories>>();
        // Fetching the mfs from another sheet.
        HSSFSheet sheetMf = wb.getSheetAt(4);
        Iterator<Row> iteratorMf = sheetMf.iterator();
        ArrayList<Accessories> al = new ArrayList<Accessories>();
        // String id_prod = null;
        while (iteratorMf.hasNext()) {
            Row rowMf = iteratorMf.next();
            // Iterator<Cell> cellItr1 = rowMf.cellIterator();
            // Cell cell1 = cellItr1.next();
            // Iterator cells1 = rowMf.cellIterator();
            // cell = (HSSFCell) cells1.next();
            String sno = new DataFormatter().formatCellValue(rowMf.getCell(0));
            System.out.println(sno + " -- ");

            if (hm3.get(sno) == null) {
                hm3.put(sno, new ArrayList());
            }

            // if(sno != null && sno.equals(id_prod)) {
            System.out.println("Got row: ");
            String name = new DataFormatter().formatCellValue(rowMf.getCell(1));
            String size = new DataFormatter().formatCellValue(rowMf.getCell(2));
            String qty = new DataFormatter().formatCellValue(rowMf.getCell(3));
            Accessories acc1 = new Accessories(name, size, qty);
            System.out.println(acc1);
            // mfar[count] = mf1;
            // count++;
            ArrayList r = (ArrayList) hm3.get(sno);
            r.add(acc1);

        }// End of while comp
        return hm3;

    }
    // Callback method to begin receiving the upload.

    @Override
    public void uploadFailed(Upload.FailedEvent event) {
        Notification.show(event.getFilename() + "----" + event.getMIMEType());

    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
/*		Notification.show(event.getFilename() + "----" + event.getMIMEType() + "Uploaded" );
*//*		final FileResource fileResource =
                new FileResource(file);
		getVerticalLayout_2().addComponent(new Embedded("", fileResource));
		*/

        Upload();

    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        // TODO Auto-generated method stub
        FileOutputStream fos = null; // Output stream to write to
        file = new File(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() +"/Jsonfile/" + filename);
/*		Notification.show(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath());
*/		FILE_PATH = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() +"/Jsonfile/" + filename;
        try{



            fos = new FileOutputStream(file);
            Notification.show("Succesfully converted :)");
        }
        catch (final java.io.FileNotFoundException e) {
            // Error while opening the file. Not reported here.
            e.printStackTrace();
            return null;
        }

        return fos; // Return the output stream to write to

    }
}
