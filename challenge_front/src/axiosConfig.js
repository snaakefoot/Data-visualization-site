import axios from 'axios';

const axiosInstance = axios.create({
   // Your API base URL
  headers: {
    'Content-Type': 'application/json',
    // Set Authorization header with token from localStorage
    'Authorization': `${localStorage.getItem('token')}`
  }
});

export default axiosInstance;