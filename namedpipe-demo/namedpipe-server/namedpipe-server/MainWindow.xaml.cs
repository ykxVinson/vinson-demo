using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace namedpipe_server
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private NamedPipeServer m_serverNamedPipe;
        public MainWindow()
        {
            InitializeComponent();

            Task.Factory.StartNew(() =>
            {
                m_serverNamedPipe = new NamedPipeServer("Presnter_NamedPipe");
                m_serverNamedPipe.Readed += OnPipeReadMsg;
                m_serverNamedPipe.Start();
            });
        }

        /// <summary>
        /// 收到管道数据
        /// </summary>
        /// <param name="arg"></param>
        /// <returns></returns>
        public string OnPipeReadMsg(string arg)
        {
            this.Dispatcher.Invoke(() =>
            {
                this.lstbox.Items.Add(arg);
            });
            return "";
        }
    }
}
