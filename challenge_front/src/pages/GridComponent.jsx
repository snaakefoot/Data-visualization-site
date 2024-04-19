import React, { useState, useEffect } from 'react';
import { DataGrid } from '@mui/x-data-grid';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import Button from '@mui/material/Button';
import axios from '../axiosConfig'; 
import { useNavigate } from 'react-router-dom';
import { itemLabel, itemTypeLabel, subclassLabel, classLabel, departmentLabel, divisionLabel, regionLabel, channelLabel, storeLabel,skuLabel, reverseItemLabels, reverseClassLabels, reverseDepartmentLabels, reverseDivisionLabels, reverseskuLabels, reverseStoreLabels } from '../labelsMappings';
import backgroundImage from './Cognira-logo-blue.png';
export const GridComponent = () => {
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsCode, setRowsCode] = useState([]);
  const [oldpage, setoldpage] = useState(-1);
  const [oldpagestarter, setoldpagestarter] = useState([['','']]);
  const [nbOfRows,setNbOfRows]= useState(100)
  const [nbOfRowsPerPage,setnbOfRowsPerPage]= useState(100)
  const [classFilter, setClassFilter] = useState('Any');
  const [channelFilter, setChannelFilter] = useState('Any');
  const [departmentFilter, setDepartmentFilter] = useState('Any');
  const [divisionFilter, setDivisionFilter] = useState('Any');
  const [itemFilter, setItemFilter] = useState('Any');
  const [itemTypeFilter, setItemTypeFilter] = useState('Any');
  const [regionFilter, setRegionFilter] = useState('Any');
  const [skuFilter, setSkuFilter] = useState('Any');
  const [storeFilter, setStoreFilter] = useState('Any');
  const [subclassFilter, setSubclassFilter] = useState('Any');
  const backEndpoint = process.env.REACT_APP_BACK_ENDPOINT;
  const navigate = useNavigate();
  useEffect(() => {
    fetchNbOfRows()
    fetchData('','');
  }
  , []);
  const fetchNbOfRows= async () => {
    let baseURL;
    try {
      if (backEndpoint) {
        // If BACK_ENDPOINT is set, use it in the URL
         baseURL  = `http://${backEndpoint}/secureEndpointN`;
    } else {
        // If BACK_ENDPOINT is not set, use localhost
         baseURL  = 'http://localhost:8080/secureEndpointN';
    }

        const queryParams = new URLSearchParams({
          chnl: channelFilter || "Any",
          str: storeFilter || "Any",
          regn: regionFilter || "Any",
          sku: skuFilter || "Any",
          item: itemFilter || "Any",
          item_type: itemTypeFilter || "Any",
          subclass: subclassFilter || "Any",
          Class: classFilter || "Any",
          dept: departmentFilter || "Any",
          dvsn: divisionFilter || "Any",
          limit: 1000000,
          lastsku: '',
          laststr: ''
        });

      const finalURL = `${baseURL}?${queryParams.toString()}`;
      const response = await axios.get(finalURL);
      const data = await response.data;

      setNbOfRows(data)
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };
  const mapRowsToLabels = (row) => {
    return {
      item: itemLabel[row.item] || 'Unknown',
      item_type: itemTypeLabel[row.item_type] || 'Unknown',
      subclass: subclassLabel[row.subclass] || 'Unknown',
      Class: classLabel[row.Class] || 'Unknown',
      dept: departmentLabel[row.dept] || 'Unknown',
      dvsn: divisionLabel[row.dvsn] || 'Unknown',
       
        regn: regionLabel[row.regn] || 'Unknown',
        chnl: channelLabel[row.chnl] || 'Unknown',
        str: storeLabel[row.str] || 'Unknown',
        sku: skuLabel[row.sku] || 'Unknown',
        sales:row.sales
    };
};
  const fetchData = async (lastsku,laststr ) => {
    let baseURL;
    try {

      if (backEndpoint) {
        // If BACK_ENDPOINT is set, use it in the URL
         baseURL  = `http://${backEndpoint}/secureEndpoint`;
    } else {
        // If BACK_ENDPOINT is not set, use localhost
         baseURL  = 'http://localhost:8080/secureEndpoint';
    }
        const queryParams = new URLSearchParams({
          chnl: channelFilter || "Any",
          str: storeFilter || "Any",
          regn: regionFilter || "Any",
          sku: skuFilter || "Any",
          item: itemFilter || "Any",
          item_type: itemTypeFilter || "Any",
          subclass: subclassFilter || "Any",
          Class: classFilter || "Any",
          dept: departmentFilter || "Any",
          dvsn: divisionFilter || "Any",
          limit: nbOfRowsPerPage || "Any",
          lastsku: lastsku,
          laststr: laststr
        });

      const finalURL = `${baseURL}?${queryParams.toString()}`;

      const response = await axios.get(finalURL);
     
      const data = await response.data;
      setRowsCode(data)
      setRows(data.map(row => mapRowsToLabels(row))); // Assuming your backend returns an array of rows
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const columns = [
    { field: 'sku', headerName: 'SKU',minWidth:150,disableReorder:true },
    { field: 'str', headerName: 'Store' ,minWidth:100,disableReorder:true},
    { field: 'item', headerName: 'Item' ,minWidth:150,disableReorder:true},
    { field: 'item_type', headerName: 'Item Type',minWidth:100,disableReorder:true },
    { field: 'chnl', headerName: 'Channel',minWidth:100,disableReorder:true },
    { field: 'Class', headerName: 'Class' ,minWidth:100,disableReorder:true},
    { field: 'subclass', headerName: 'Subclass' ,minWidth:100,disableReorder:true},
    { field: 'dept', headerName: 'Department',minWidth:150,disableReorder:true },
    { field: 'dvsn', headerName: 'Division' ,minWidth:100,disableReorder:true},  
    { field: 'regn', headerName: 'Region' ,minWidth:100,disableReorder:true},
    { field: 'sales', headerName: 'Sales',minWidth:100,disableReorder:true },
  ];
  const getRowId = (row) => `${row.sku}-${row.str}`;
  
  return ( 
    <div>
      <div className="image-container" style={{marginTop:'30px'}} >
                <img src={backgroundImage} alt="Background" style={{ width: '400px', height: 'auto' }} />
      </div>
      <div style={{ textAlign: 'center', marginBottom: '10px', color: 'white', fontSize: '24px' }}>
        <h1>Welcome Retailer</h1>
      </div>
      <div style={{ display: 'flex', alignItems: 'center' , marginBottom: '10px'}}>
<Autocomplete
  disablePortal
  id="Sku"
  options={Object.values(skuLabel)}
  sx={{ width: 200 }}
  renderInput={(params) => <TextField {...params} label="Sku Label" />}
  onChange={(event, value) => {
   setSkuFilter(reverseskuLabels[value])
  }}
/>

<Autocomplete
  disablePortal
  id="str"
  options={Object.values(storeLabel)}
  sx={{ width: 200 }}
  renderInput={(params) => <TextField {...params} label="Store Label" />}
  onChange={(event, value) => {
    setStoreFilter(reverseStoreLabels[value])
  }}
/>


<Autocomplete
  disablePortal
  id="itemLabel"
  options={Object.values(itemLabel)}
  sx={{ width: 200 }}
  renderInput={(params) => <TextField {...params} label="Item Label" />}
  onChange={(event, value) => {
    setItemFilter(reverseItemLabels[value])
  }}
  disabled={ (skuFilter !== "Any" && skuFilter) }
/>

<Autocomplete
  disablePortal
  id="class-label"
  options={Object.values(classLabel)}
  sx={{ width: 200 }}
  renderInput={(params) => <TextField {...params} label="Class Label" />}
  onChange={(event, value) => {
    setClassFilter((reverseClassLabels[value]))
  }}
  disabled={ (skuFilter !== "Any" && skuFilter) }
/>

<Autocomplete
  disablePortal
  id="department-label"
  options={Object.values(departmentLabel)}
  sx={{ width: 200 }}
  renderInput={(params) => <TextField {...params} label="Department Label" />}
  onChange={(event, value) => {
    setDepartmentFilter(reverseDepartmentLabels[value])
  }}
  disabled={ (storeFilter !== "Any" && storeFilter) }
/>

<Autocomplete
  disablePortal
  id="division-label"
  options={Object.values(divisionLabel)}
  sx={{ width: 200 }}
  renderInput={(params) => <TextField {...params} label="Division Label" />}
  onChange={(event, value) => {
    setDivisionFilter(reverseDivisionLabels[value])
  }}
  disabled={(departmentFilter !== "Any" && departmentFilter) || (storeFilter !== "Any" && storeFilter) }
/>
<Button size="small" onClick={() => {
  fetchNbOfRows()
  fetchData('', '')
  setPage(0)
  setoldpagestarter([['','']])
  }} sx={{ backgroundColor: 'white', color: 'black', marginLeft: '5px' }}>
      Filter
    </Button>
</div>

      <div style={{ width: '100%', backgroundColor: 'white' }}>
      <DataGrid
        rows={rows}
        columns={columns}
        paginationModel={{page:page,pageSize: 100}}
        getRowId={getRowId}
        paginationMode="server"
        rowCount={nbOfRows}
        pageSize= {100}
        
         disableColumnResize={false}
        pageSizeOptions={[nbOfRowsPerPage]}
        onPaginationModelChange  ={(params) => {
            setPage(params.page)
          if(params.page>oldpage){
            if(oldpagestarter.length===params.page-1){
               setoldpagestarter(oldpagestarter.concat([[rowsCode[0].sku,rowsCode[0].str] ])) 
            
            }
            
          fetchData( rowsCode[rowsCode.length - 1].sku,rowsCode[rowsCode.length - 1].str+"A")
          }
          else{
            fetchData(oldpagestarter[params.page][0],oldpagestarter[params.page][1])
          }
          setoldpage(params.page)
          
        }}
         // This will remove the column filtering option
        disableColumnReorder={true}
        disableColumnFilter={true}
        autoHeight   // This will remove the column sorting option
        onRowClick={(row) => {
   
          navigate(`/salesOverTime?sku=${reverseskuLabels[row.row.sku]}&str=${reverseStoreLabels[row.row.str]}` );
        }
      }
      />
      </div>
    </div>
  );
};

