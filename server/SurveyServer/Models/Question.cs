using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SurveyServer.Models
{
    public enum QuestionType{
        Single,
        Multiple,
        Input
    }
    public class Question
    {
        [Key]
        public string QuestionKey { get; set; }

        [Required,Display(Name ="Number")]
        public int QuestionNum { get; set; }

        [Required,StringLength(50),Display(Name ="Description")]
        public string QuestionDescription { get; set; }

        [ForeignKey("Survey"),Display(Name ="Survey ID")]
        public string RefSurveyID { get; set; }

        public Survey Survey { get; set; }
        
        [Required,Display(Name ="Type")]

        public QuestionType QuestionType { get; set; }

        [StringLength(450)]
        public string Options { get; set; }
    }
}
