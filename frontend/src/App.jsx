
import { useEffect, useState } from 'react'
import {
  Activity,
  AlertTriangle,
  CheckCircle2,
  CircleOff,
  Clock,
  Server,
  ShieldCheck,
  Wifi,
  ArrowLeft,
} from 'lucide-react'

import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'

import './App.css'

function App() {
  const [devices, setDevices] = useState({})
  const [alerts, setAlerts] = useState([])
  const [history, setHistory] = useState([])
  const [connection, setConnection] = useState('Connecting')
  const [currentPage, setCurrentPage] = useState('Dashboard')

  // WebSocket connection
  useEffect(() => {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    const socket = new WebSocket(
      `${protocol}//${window.location.host}/ws/network`
    )

    socket.onopen = () => setConnection('Connected')
    socket.onclose = () => setConnection('Disconnected')
    socket.onerror = () => setConnection('Connection error')

    socket.onmessage = (message) => {
      try {
        const event = JSON.parse(message.data)
        const data = event.data

        if (event.type === 'MONITORING_DATA') {
          setDevices((previous) => ({
            ...previous,
            [data.device]: data,
          }))

          setHistory((previous) => [
            ...previous.slice(-19),
            {
              time: new Date().toLocaleTimeString(),
              latency: Number(data.latency) || 0,
            },
          ])
        }

        if (event.type === 'ALERT_EVENT') {
          setAlerts((previous) => [data, ...previous].slice(0, 20))
        }
      } catch (error) {
        console.error('Could not process WebSocket message:', error)
      }
    }

    return () => socket.close()
  }, [])

  const deviceList = Object.values(devices)

  const online = deviceList.filter(
    (device) => String(device.status).toUpperCase() === 'UP'
  ).length

  const offline = deviceList.filter(
    (device) => String(device.status).toUpperCase() === 'DOWN'
  ).length

  const averageLatency = deviceList.length
    ? (
        deviceList.reduce(
          (total, device) => total + (Number(device.latency) || 0),
          0
        ) / deviceList.length
      ).toFixed(1)
    : '0.0'

  const pageTitles = {
    Dashboard: 'Network Dashboard',
    Devices: 'Network Devices',
    Alerts: 'Network Alerts',
  }

  return (
    <div className="dashboard">
      {/* SIDEBAR */}
      <aside className="sidebar">

        <div className="brand">
          <div className="brand-icon">
            <ShieldCheck size={25} />
          </div>

          <div>
            <h2>NetGuard</h2>
            <span>Network Intelligence</span>
          </div>
        </div>

        <div className="nav-label">MAIN MENU</div>

        <button
          type="button"
          className={`nav-item ${currentPage === 'Dashboard' ? 'active' : ''}`}
          onClick={() => setCurrentPage('Dashboard')}
        >
          <Activity size={19} />
          Dashboard
        </button>

        <button
          type="button"
          className={`nav-item ${currentPage === 'Devices' ? 'active' : ''}`}
          onClick={() => setCurrentPage('Devices')}
        >
          <Server size={19} />
          Devices
        </button>

        <button
          type="button"
          className={`nav-item ${currentPage === 'Alerts' ? 'active' : ''}`}
          onClick={() => setCurrentPage('Alerts')}
        >
          <AlertTriangle size={19} />
          Alerts

          {alerts.length > 0 && (
            <span className="nav-count">{alerts.length}</span>
          )}
        </button>

        <div className="sidebar-bottom">
          <div className="connection-label">BACKEND CONNECTION</div>

          <div className="connection-status">
            <span
              className={`status-dot ${
                connection === 'Connected' ? 'green' : 'red'
              }`}
            />
            {connection}
          </div>

          <p>NetGuard Monitoring System</p>
        </div>
      </aside>

      {/* MAIN CONTENT */}
      <main className="main-content">

        <header className="topbar">
          <div>
            <h1>{pageTitles[currentPage]}</h1>
            <p>Real-time network health and performance monitoring</p>
          </div>

          <div className="live-indicator">
            <span className="status-dot green" />
            LIVE MONITORING
          </div>
        </header>

        {/* DASHBOARD PAGE */}
        {currentPage === 'Dashboard' && (
          <>
            <section className="stats-grid">

              <StatCard
                title="Total Devices"
                value={deviceList.length}
                icon={<Server />}
                color="blue"
              />

              <StatCard
                title="Online Devices"
                value={online}
                icon={<CheckCircle2 />}
                color="green"
              />

              <StatCard
                title="Offline Devices"
                value={offline}
                icon={<CircleOff />}
                color="red"
              />

              <StatCard
                title="Average Latency"
                value={`${averageLatency} ms`}
                icon={<Activity />}
                color="purple"
              />

            </section>

            <section className="content-grid">

              <div className="panel chart-panel">
                <div className="panel-heading">
                  <div>
                    <h2>Network Latency</h2>
                    <p>Recent monitoring updates</p>
                  </div>

                  <span className="panel-icon">
                    <Activity size={19} />
                  </span>
                </div>

                <div className="chart">
                  {history.length === 0 ? (
                    <EmptyState message="Waiting for monitoring data..." />
                  ) : (
                    <ResponsiveContainer width="100%" height="100%">
                      <AreaChart data={history}>
                        <defs>
                          <linearGradient
                            id="latencyFill"
                            x1="0"
                            y1="0"
                            x2="0"
                            y2="1"
                          >
                            <stop
                              offset="0%"
                              stopColor="#4f8cff"
                              stopOpacity={0.3}
                            />
                            <stop
                              offset="95%"
                              stopColor="#4f8cff"
                              stopOpacity={0}
                            />
                          </linearGradient>
                        </defs>

                        <CartesianGrid
                          strokeDasharray="3 3"
                          stroke="#263248"
                        />

                        <XAxis
                          dataKey="time"
                          stroke="#8492a8"
                          tick={{ fontSize: 11 }}
                        />

                        <YAxis
                          stroke="#8492a8"
                          tick={{ fontSize: 11 }}
                        />

                        <Tooltip
                          contentStyle={{
                            background: '#172235',
                            border: '1px solid #334155',
                            borderRadius: 8,
                          }}
                        />

                        <Area
                          type="monotone"
                          dataKey="latency"
                          name="Latency (ms)"
                          stroke="#4f8cff"
                          fill="url(#latencyFill)"
                          strokeWidth={2}
                        />
                      </AreaChart>
                    </ResponsiveContainer>
                  )}
                </div>
              </div>

              <div className="panel">
                <div className="panel-heading">
                  <div>
                    <h2>System Overview</h2>
                    <p>Current monitoring status</p>
                  </div>

                  <Wifi size={20} color="#4f8cff" />
                </div>

                <div className="overview-item">
                  <span>Backend connection</span>
                  <strong
                    className={
                      connection === 'Connected'
                        ? 'green-text'
                        : 'red-text'
                    }
                  >
                    {connection}
                  </strong>
                </div>

                <div className="overview-item">
                  <span>Monitored devices</span>
                  <strong>{deviceList.length}</strong>
                </div>

                <div className="overview-item">
                  <span>Active offline devices</span>
                  <strong className="red-text">{offline}</strong>
                </div>

                <div className="overview-item">
                  <span>Received alerts</span>
                  <strong>{alerts.length}</strong>
                </div>
              </div>

            </section>

            <section className="panel table-panel">
              <div className="panel-heading">
                <div>
                  <h2>Device Monitoring</h2>
                  <p>Live status of monitored network devices</p>
                </div>

                <Server size={20} color="#4f8cff" />
              </div>

              <DeviceTable deviceList={deviceList} />
            </section>

            <section className="panel alerts-panel">
              <div className="panel-heading">
                <div>
                  <h2>Recent Alerts</h2>
                  <p>Latest network events and notifications</p>
                </div>

                <AlertTriangle size={20} color="#f5a623" />
              </div>

              <AlertList alerts={alerts.slice(0, 5)} />
            </section>
          </>
        )}

        {/* DEVICES PAGE */}
        {currentPage === 'Devices' && (
          <section className="panel table-panel">

            <div className="panel-heading">
              <div>
                <h2>All Monitored Devices</h2>
                <p>Live status of all network devices</p>
              </div>

              <Server size={20} color="#4f8cff" />
            </div>

            <div className="stats-grid">
              <StatCard
                title="Total Devices"
                value={deviceList.length}
                icon={<Server />}
                color="blue"
              />

              <StatCard
                title="Online Devices"
                value={online}
                icon={<CheckCircle2 />}
                color="green"
              />

              <StatCard
                title="Offline Devices"
                value={offline}
                icon={<CircleOff />}
                color="red"
              />
            </div>

            <DeviceTable deviceList={deviceList} />

          </section>
        )}

        {/* ALERTS PAGE */}
        {currentPage === 'Alerts' && (
          <section className="panel alerts-panel">

            <div className="panel-heading">
              <div>
                <h2>All Received Alerts</h2>
                <p>Live network failures and warning notifications</p>
              </div>

              <AlertTriangle size={20} color="#f5a623" />
            </div>

            <div className="overview-item">
              <span>Total received alerts</span>
              <strong>{alerts.length}</strong>
            </div>

            <AlertList alerts={alerts} />

          </section>
        )}

        <footer>
          <Clock size={14} />
          NetGuard · Real-Time Network Failure Detection & Alert System
        </footer>

      </main>
    </div>
  )
}

// DEVICE TABLE
function DeviceTable({ deviceList }) {
  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>
            <th>DEVICE / IP ADDRESS</th>
            <th>STATUS</th>
            <th>LATENCY</th>
            <th>PACKET LOSS</th>
            <th>RESPONSE TIME</th>
          </tr>
        </thead>

        <tbody>
          {deviceList.length === 0 ? (
            <tr>
              <td colSpan="5" className="empty-cell">
                Waiting for devices from the backend...
              </td>
            </tr>
          ) : (
            deviceList.map((device) => {
              const isUp =
                String(device.status).toUpperCase() === 'UP'

              return (
                <tr key={device.device}>
                  <td className="device-name">
                    <Server size={17} />
                    {device.device}
                  </td>

                  <td>
                    <span
                      className={`status-badge ${
                        isUp ? 'online' : 'offline'
                      }`}
                    >
                      {isUp ? '● Online' : '● Offline'}
                    </span>
                  </td>

                  <td>{device.latency ?? '—'} ms</td>
                  <td>{device.packetLoss ?? '—'}%</td>
                  <td>{device.responseTime ?? '—'} ms</td>
                </tr>
              )
            })
          )}
        </tbody>
      </table>
    </div>
  )
}

// ALERT LIST
function AlertList({ alerts }) {
  if (alerts.length === 0) {
    return <EmptyState message="No alerts received yet." />
  }

  return (
    <div className="alert-list">
      {alerts.map((alert, index) => (
        <div
          className="alert-item"
          key={`${alert.timestamp}-${index}`}
        >
          <AlertTriangle size={19} color="#f5a623" />

          <div className="alert-details">
            <strong>
              {alert.event || 'Network event'} — {alert.device}
            </strong>

            <p>{alert.reason || 'No additional details'}</p>
          </div>

          <span className="alert-severity">
            {alert.severity || 'INFO'}
          </span>
        </div>
      ))}
    </div>
  )
}

// STATISTICS CARD
function StatCard({ title, value, icon, color }) {
  return (
    <div className="stat-card">
      <div>
        <p>{title}</p>
        <h2>{value}</h2>
      </div>

      <div className={`stat-icon ${color}`}>
        {icon}
      </div>
    </div>
  )
}

// EMPTY STATE
function EmptyState({ message }) {
  return <div className="empty-state">{message}</div>
}

export default App
