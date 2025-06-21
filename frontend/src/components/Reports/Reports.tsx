import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Download, FileText, Calendar, Filter, Mail, Printer } from 'lucide-react';
import { format } from 'date-fns';
import { reportingAPI } from '../../services/api';
import toast from 'react-hot-toast';

const Reports: React.FC = () => {
  const [selectedReport, setSelectedReport] = useState('portfolio');
  const [dateRange, setDateRange] = useState({
    start: format(new Date(Date.now() - 30 * 24 * 60 * 60 * 1000), 'yyyy-MM-dd'),
    end: format(new Date(), 'yyyy-MM-dd')
  });
  const [generating, setGenerating] = useState(false);

  const reportTypes = [
    {
      id: 'portfolio',
      name: 'Portfolio Summary',
      description: 'Complete portfolio overview with positions and performance',
      icon: FileText
    },
    {
      id: 'risk',
      name: 'Risk Report',
      description: 'Detailed risk metrics and exposure analysis',
      icon: FileText
    },
    {
      id: 'performance',
      name: 'Performance Attribution',
      description: 'Performance breakdown by strategy and asset class',
      icon: FileText
    },
    {
      id: 'trades',
      name: 'Trade Blotter',
      description: 'Complete trade history and execution details',
      icon: FileText
    }
  ];

  const recentReports = [
    {
      name: 'Portfolio Summary - December 2024',
      type: 'Portfolio',
      date: '2024-12-15',
      size: '2.4 MB',
      status: 'Ready'
    },
    {
      name: 'Risk Analysis - Q4 2024',
      type: 'Risk',
      date: '2024-12-10',
      size: '1.8 MB',
      status: 'Ready'
    },
    {
      name: 'Performance Attribution - November',
      type: 'Performance',
      date: '2024-11-30',
      size: '3.1 MB',
      status: 'Ready'
    },
    {
      name: 'Trade Blotter - Week 50',
      type: 'Trades',
      date: '2024-12-08',
      size: '856 KB',
      status: 'Ready'
    }
  ];

  const generateReport = async () => {
    setGenerating(true);
    try {
      const response = await reportingAPI.generateReport({
        reportType: selectedReport,
        startDate: dateRange.start,
        endDate: dateRange.end,
        format: 'pdf'
      });
      
      toast.success(`Report generated successfully! ID: ${response.data}`);
    } catch (error) {
      console.error('Failed to generate report:', error);
      toast.error('Failed to generate report');
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="text-3xl font-bold text-white">Reports & Documentation</h1>
          <p className="text-gray-400 mt-1">Generate comprehensive reports and export portfolio data</p>
        </div>
        <div className="flex items-center space-x-3">
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="flex items-center space-x-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium"
          >
            <Mail className="h-4 w-4" />
            <span>Schedule Reports</span>
          </motion.button>
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={generateReport}
            disabled={generating}
            className="flex items-center space-x-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg font-medium disabled:opacity-50"
          >
            <Download className="h-4 w-4" />
            <span>{generating ? 'Generating...' : 'Generate Report'}</span>
          </motion.button>
        </div>
      </motion.div>

      {/* Report Configuration */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
          <Filter className="h-5 w-5 mr-2" />
          Report Configuration
        </h3>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <label className="block text-sm font-medium text-gray-300 mb-3">Report Type</label>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              {reportTypes.map((report) => (
                <motion.div
                  key={report.id}
                  whileHover={{ scale: 1.02 }}
                  onClick={() => setSelectedReport(report.id)}
                  className={`p-4 border rounded-lg cursor-pointer transition-all ${
                    selectedReport === report.id
                      ? 'border-blue-500 bg-blue-600/20'
                      : 'border-gray-600 bg-gray-700/50 hover:border-gray-500'
                  }`}
                >
                  <div className="flex items-start space-x-3">
                    <report.icon className={`h-5 w-5 mt-0.5 ${
                      selectedReport === report.id ? 'text-blue-400' : 'text-gray-400'
                    }`} />
                    <div>
                      <h4 className={`font-medium ${
                        selectedReport === report.id ? 'text-blue-400' : 'text-white'
                      }`}>
                        {report.name}
                      </h4>
                      <p className="text-gray-400 text-sm mt-1">{report.description}</p>
                    </div>
                  </div>
                </motion.div>
              ))}
            </div>
          </div>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Date Range</label>
              <div className="space-y-3">
                <input
                  type="date"
                  value={dateRange.start}
                  onChange={(e) => setDateRange(prev => ({ ...prev, start: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                />
                <input
                  type="date"
                  value={dateRange.end}
                  onChange={(e) => setDateRange(prev => ({ ...prev, end: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Format</label>
              <select className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500">
                <option value="pdf">PDF Document</option>
                <option value="excel">Excel Spreadsheet</option>
                <option value="csv">CSV Data</option>
              </select>
            </div>

            <div className="p-4 bg-blue-600/20 border border-blue-500/30 rounded-lg">
              <p className="text-blue-400 text-sm">
                <strong>Note:</strong> Reports are generated in real-time and may take a few moments for complex datasets.
              </p>
            </div>
          </div>
        </div>
      </motion.div>

      {/* Recent Reports */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
          <Calendar className="h-5 w-5 mr-2" />
          Recent Reports
        </h3>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-gray-700">
                <th className="text-left py-3 px-4 text-gray-400 font-medium">Report Name</th>
                <th className="text-left py-3 px-4 text-gray-400 font-medium">Type</th>
                <th className="text-left py-3 px-4 text-gray-400 font-medium">Generated</th>
                <th className="text-left py-3 px-4 text-gray-400 font-medium">Size</th>
                <th className="text-left py-3 px-4 text-gray-400 font-medium">Status</th>
                <th className="text-right py-3 px-4 text-gray-400 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody>
              {recentReports.map((report, index) => (
                <tr key={index} className="border-b border-gray-700/50 hover:bg-gray-700/30">
                  <td className="py-4 px-4">
                    <div className="flex items-center space-x-3">
                      <FileText className="h-5 w-5 text-blue-400" />
                      <span className="text-white font-medium">{report.name}</span>
                    </div>
                  </td>
                  <td className="py-4 px-4">
                    <span className="px-2 py-1 bg-gray-700 text-gray-300 rounded text-sm">
                      {report.type}
                    </span>
                  </td>
                  <td className="py-4 px-4 text-gray-300">
                    {format(new Date(report.date), 'MMM dd, yyyy')}
                  </td>
                  <td className="py-4 px-4 text-gray-300">{report.size}</td>
                  <td className="py-4 px-4">
                    <span className="px-2 py-1 bg-green-600/20 text-green-400 rounded text-sm">
                      {report.status}
                    </span>
                  </td>
                  <td className="py-4 px-4">
                    <div className="flex items-center justify-end space-x-2">
                      <motion.button
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.95 }}
                        className="p-2 text-gray-400 hover:text-blue-400 transition-colors"
                        title="Download"
                      >
                        <Download className="h-4 w-4" />
                      </motion.button>
                      <motion.button
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.95 }}
                        className="p-2 text-gray-400 hover:text-gray-300 transition-colors"
                        title="Print"
                      >
                        <Printer className="h-4 w-4" />
                      </motion.button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </motion.div>

      {/* Report Templates */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
        className="grid grid-cols-1 md:grid-cols-3 gap-6"
      >
        <div className="bg-gray-800 border border-gray-700 rounded-xl p-6">
          <h4 className="text-lg font-semibold text-white mb-3">ISDA Documentation</h4>
          <p className="text-gray-400 text-sm mb-4">
            Generate ISDA-compliant trade confirmations and master agreements for structured products.
          </p>
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium"
          >
            Generate ISDA Docs
          </motion.button>
        </div>

        <div className="bg-gray-800 border border-gray-700 rounded-xl p-6">
          <h4 className="text-lg font-semibold text-white mb-3">Regulatory Reports</h4>
          <p className="text-gray-400 text-sm mb-4">
            Automated compliance reporting for regulatory requirements and audit trails.
          </p>
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            className="w-full py-2 px-4 bg-green-600 hover:bg-green-700 text-white rounded-lg font-medium"
          >
            Generate Compliance
          </motion.button>
        </div>

        <div className="bg-gray-800 border border-gray-700 rounded-xl p-6">
          <h4 className="text-lg font-semibold text-white mb-3">Client Statements</h4>
          <p className="text-gray-400 text-sm mb-4">
            Professional client statements with performance metrics and position details.
          </p>
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            className="w-full py-2 px-4 bg-yellow-600 hover:bg-yellow-700 text-white rounded-lg font-medium"
          >
            Generate Statements
          </motion.button>
        </div>
      </motion.div>
    </div>
  );
};

export default Reports;