using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SurveyServer.Models
{
    public class Response
    {
        [Key]
        public string ResponseID { get; set; }

        public long TimeStamp { get; set; }
        public long Longitude { get; set; }
        public long Latitude { get; set; }
        
        [StringLength(50)]
        public string IMEI { get; set; }
        public ICollection<Answer> Answers { get; set; }


        [ForeignKey("Survey"),Display(Name ="Survey ID")]
        public string RefSurveyID { get; set; }
        public Survey Survey { get; set; }
    }
}
