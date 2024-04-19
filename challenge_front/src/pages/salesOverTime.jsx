import backgroundImage from './Cognira-logo-blue.png';
import {  useLocation } from 'react-router-dom';
import { DataGrid } from '@mui/x-data-grid';
import { DateRangePicker } from 'rsuite';
import axios from '../axiosConfig';
import 'ag-grid-charts-enterprise';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-quartz.css';
import { AgGridReact } from 'ag-grid-react';
import React, { useEffect, useCallback, useMemo, useState } from 'react';
import './style.css';
import 'rsuite/DateRangePicker/styles/index.css';

const backEndpoint = process.env.REACT_APP_BACK_ENDPOINT;
export const SalesOverTime = () => {
  useEffect(() => {
    fetchData();
  }, []);
  
  const [rows, setRows] = useState([]);
  const [nbOfRowsPerPage, setnbOfRowsPerPage] = useState(100);
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const [start, setStart] = useState(new Date(2000, 1, 1, 0, 0, 0));
  const [end, setEnd] = useState(new Date(2040, 1, 1, 0, 0, 0));
  const [sku, setSku] = useState(queryParams.get('sku'));
  const [str, setstr] = useState(queryParams.get('str'));

  useEffect(() => {
    fetchData();
  }, [end]);

  const handleDateRangeChange = (value) => {
    if (value) {
      setStart(value[0]);
      setEnd(value[1]);
    } else {
      setStart(new Date(2000, 1, 1, 0, 0, 0));
      setEnd(new Date(2040, 1, 1, 0, 0, 0));
    }
    fetchData();
  };

  const numberParser = (params) => {
    const value = params.newValue;
    if (value === null || value === undefined || value === '') {
      return null;
    }
    return parseFloat(value);
  };

  const fetchData = async () => {
    let baseURL;
    if (backEndpoint) {
      // If BACK_ENDPOINT is set, use it in the URL
       baseURL  = `http://${backEndpoint}/secureEndpoint3`;
  } else {
      // If BACK_ENDPOINT is not set, use localhost
       baseURL  = 'http://localhost:8080/secureEndpoint3';
  }
    const startDate = start || new Date(2000, 1, 1, 0, 0, 0);
    const endDate = end || new Date(2040, 1, 1, 0, 0, 0);
    const lastDayDate = start || new Date(2000, 1, 1, 0, 0, 0);
    try {
      
      const queryParams = new URLSearchParams({
        sku: sku,
        str: str,
        start: startDate.getDate() + (startDate.getMonth() + 1) + 1 * 100 + (startDate.getFullYear()) * 10000,
        end: endDate.getDate() + (endDate.getMonth() + 1) * 100 + (endDate.getFullYear()) * 10000,
        lastday: lastDayDate.getDate() + (lastDayDate.getMonth() + 1) * 100 + (lastDayDate.getFullYear()) * 10000,
        limit: 100000,
      });

      const finalURL = `${baseURL}?${queryParams.toString()}`;

      const response = await axios.get(finalURL);
      const data = await response.data;

      const X = data.map(row => ({
        date: new Date(row['datenum'] / 10000, (row['datenum'] / 100) % 100, row['datenum'] % 100, 0, 0, 0),
        sales: row['sales']
      }));
      setRows(X);

      return X; // Assuming your backend returns an array of rows
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const chartTooltipRenderer = ({ datum, xKey, yKey }) => {
    return {
      content: `${formatDate(datum[xKey])}: ${datum[yKey]}`,
    };
  };

  const formatDate = (date) => {
    return Intl.DateTimeFormat('en-GB', {
      day: '2-digit',
      month: 'short',
      year: undefined,
    }).format(new Date(date));
  };

  const Grid = () => {
    const containerStyle = useMemo(() => ({ width: '100%', height: '100%' }), []);
    const gridStyle = useMemo(() => ({ height: '100%', width: '100%' }), []);

    const [columnDefs, setColumnDefs] = useState([
      {
        field: 'date',
        chartDataType: 'time',
        valueFormatter: (params) => params.value.toISOString().substring(0, 10),
      },
      { field: 'sales', chartDataType: 'series', valueParser: numberParser },
    ]);

    const defaultColDef = useMemo(() => ({ flex: 1 }), []);
    const popupParent = useMemo(() => document.body, []);
    const chartThemeOverrides = useMemo(() => ({
      common: {
        padding: {
          top: 45,
        },
        axes: {
          number: {
            title: {
              enabled: true,
              formatter: (params) => params.boundSeries.map((s) => s.name).join(' / '),
            },
          },
          time: {
            crosshair: {
              label: {
                renderer: (params) => ({
                  text: formatDate(params.value),
                }),
              },
            },
          },
        },
      },
      bar: {
        series: {
          strokeWidth: 2,
          fillOpacity: 0.8,
          tooltip: {
            renderer: chartTooltipRenderer,
          },
        },
      },
      line: {
        series: {
          strokeWidth: 5,
          strokeOpacity: 0.8,
          tooltip: {
            renderer: chartTooltipRenderer,
          },
        },
      },
    }), []);

    const onGridReady = useCallback((params) => {
      params.api.setGridOption('rowData', rows);
    }, []);

    const onFirstDataRendered = useCallback((params) => {
      params.api.createRangeChart({
        chartContainer: document.querySelector('#myChart'),
        cellRange: {
          columns: ['date', 'sales'],
        },
        suppressChartRanges: true,
        chartType: 'customCombo',
        aggFunc: 'sum',
      });
    }, []);

    return (
      <div style={containerStyle}>
        <div className="wrapper">
          <div style={gridStyle} className="ag-theme-quartz">
            <AgGridReact
              columnDefs={columnDefs}
              defaultColDef={defaultColDef}
              enableRangeSelection={true}
              popupParent={popupParent}
              enableCharts={true}
              chartThemeOverrides={chartThemeOverrides}
              onGridReady={onGridReady}
              onFirstDataRendered={onFirstDataRendered}
            />
          </div>
          <div id="myChart" className="ag-theme-quartz"></div>
        </div>
      </div>
    );
  };

  const columns = [
    {
      field: 'date',
      headerName: 'Date',
      minWidth: 500,
      valueFormatter: (params) => {
        const date = new Date(params.value);
        const options = { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' };
        const formattedDate = date.toLocaleDateString('en-US', options);
        return formattedDate;
      },
    },
    { field: 'sales', headerName: 'Sales', minWidth: 100 },
  ];

  const getRowId = (row) => `${row.date}-${row.sales}}`;

  return (
    <div>
      <div className="image-container" style={{ marginTop: '30px' }}>
        <img src={backgroundImage} alt="Background" style={{ width: '400px', height: 'auto' }} />
      </div>
      <div style={{ textAlign: 'center', marginBottom: '10px', color: 'white', fontSize: '24px' }}>
        <h1>Welcome Retailer</h1>
      </div>
      <div style={{ marginBottom: '20px' }}>
        <DateRangePicker onChange={handleDateRangeChange} />
      </div>
      <div style={{ width: '100%', backgroundColor: 'white' }}>
        <Grid />
        <div style={{ marginTop: '30px' }}>
          <DataGrid
            rows={rows}
            columns={columns}
            getRowId={getRowId}
            pageSize={nbOfRowsPerPage}
          />
        </div>
      </div>
    </div>
  );
};
