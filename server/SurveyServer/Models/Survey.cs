using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.ComponentModel.DataAnnotations;
namespace SurveyServer.Models
{
    public class Survey
    {
        [Key]
        public string SurveyID { get; set; }

        [StringLength(20), Required, Display(Name = "Title")]
        public string SurveyTitle { get; set; }

        [StringLength(9),Required,Display(Name = "Pattern Lock")]
        public string PatternLock { get; set; }
        public ICollection<Response> Responses { get; set; }
        public ICollection<Question> Questions { get; set; }
    }
}
